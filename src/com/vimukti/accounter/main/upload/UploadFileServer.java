package com.vimukti.accounter.main.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingQueue;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.vimukti.accounter.main.FileDeleteManager;
import com.vimukti.accounter.main.ServerConfiguration;
import com.vimukti.accounter.web.client.exception.AccounterException;

public class UploadFileServer extends Thread {
	private static LinkedBlockingQueue<UploadAttachment> queue = new LinkedBlockingQueue<UploadAttachment>();

	public synchronized static void put(UploadAttachment attachment)
			throws InterruptedException {
		queue.put(attachment);
	}

	private static byte[] encriptionKey = "123456789ABCDEFG".getBytes();

	@Override
	public void run() {
		while (true) {
			try {
				UploadAttachment take = queue.take();
				process(take);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static File getAttachmentFile(String attachmentId) throws Exception {
		File tmpfile = new File(ServerConfiguration.getEncriptTmpDir(),
				attachmentId);
		boolean exists = tmpfile.exists();
		if (!ServerConfiguration.uploadToRackSpace()) {
			return exists ? tmpfile : null;
		}
		if (!exists) {
			downloadFromRackSpace(attachmentId, tmpfile);
		}
		return tmpfile.exists() ? tmpfile : null;
	}

	private static void downloadFromRackSpace(String attachmentId, File file)
			throws Exception {
		FilesClient client = new FilesClient();
		client.login();
		String containerName = getContainerName();
		byte[] object = client.getObject(containerName, attachmentId);
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(object);
		fout.close();
	}

	private void process(UploadAttachment take) throws Exception {
		switch (take.processType) {
		case UploadAttachment.CREATE:
			uploadAttachment(take.attachmentId);
			break;
		case UploadAttachment.DELETE:
			deleteAttachment(take.attachmentId);
			break;
		default:
			break;
		}
	}

	public void deleteAttachment(String attId) throws Exception {
		if (ServerConfiguration.uploadToRackSpace()) {
			FilesClient client = new FilesClient();
			client.login();
			client.deleteObject(getContainerName(), attId);
		} else {
			File encrFile = new File(ServerConfiguration.getEncriptTmpDir(),
					attId);
			FileDeleteManager.deleteFile(encrFile);
		}
	}

	private void uploadAttachment(String attachmentId)
			throws AccounterException {
		File encrFile = checkAndReturnEncryptFile(attachmentId);
		if (encrFile == null) {
			throw new AccounterException("Unable to find the encrypted file");
		}
		if (!ServerConfiguration.uploadToRackSpace()) {
			return;
		}
		FilesClient filesClient = new FilesClient();
		try {
			filesClient.login();
			String containerName = getContainerName();
			if (!filesClient.containerExists(containerName)) {
				filesClient.createContainer(containerName);
			}

			filesClient.storeObjectAs(containerName, encrFile,
					"application/octet-stream", attachmentId);

		} catch (Exception e) {
			throw new AccounterException(e.getMessage());
		}
		FileDeleteManager.deleteFile(encrFile);

	}

	private static String getContainerName() {
		return ServerConfiguration.getAttachmentsContainerName();
	}

	private File checkAndReturnEncryptFile(String attachmentId) {
		// Check is File is in in EncryptedFolder
		File tempFile = new File(ServerConfiguration.getEncriptTmpDir(),
				attachmentId);
		if (tempFile.exists()) {
			// File is Exists in Encrypted Folder
			return tempFile;
		}
		// File Not exists in Encrypted FOlder, Check in
		// Un-EncryptedFolder
		File unencrypted = new File(ServerConfiguration.getAttachmentsDir(),
				attachmentId);
		if (!unencrypted.exists()) {
			// File Not Exists, then return
			return null;
		}
		copyWithEncryptionFile(unencrypted, tempFile, attachmentId);
		return tempFile;
	}

	private boolean copyWithEncryptionFile(File fs, File fd, String attID) {
		try {
			SecretKey key = new SecretKeySpec(encriptionKey, "AES");
			File f = new File(fd.getAbsolutePath().substring(0,
					fd.getAbsolutePath().lastIndexOf(File.separator)));
			if (!f.exists())
				f.mkdirs();
			FileInputStream stream = new FileInputStream(fs);
			FileOutputStream os = new FileOutputStream(fd);
			OutputStream fos = createCipherOutputStream(key, os);
			byte b[] = new byte[4096];
			int read = 0;
			do {
				read = stream.read(b);
				if (read != -1) {
					fos.write(b, 0, read);
				}
			} while (read != -1);
			fos.close();
			stream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	private CipherOutputStream createCipherOutputStream(SecretKey key,
			OutputStream fos) throws Exception {
		Cipher cip = null;
		cip = Cipher.getInstance("AES", "BC"); //$NON-NLS-1$ //$NON-NLS-2$
		SecureRandom ran = new SecureRandom();
		cip.init(Cipher.ENCRYPT_MODE, key, ran);
		return new CipherOutputStream(fos, cip);
	}
}
