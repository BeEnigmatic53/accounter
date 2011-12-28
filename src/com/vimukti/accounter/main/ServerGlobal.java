package com.vimukti.accounter.main;

import java.io.IOException;

import com.vimukti.accounter.web.client.AbstractGlobal;
import com.vimukti.accounter.web.client.core.ClientCompanyPreferences;
import com.vimukti.accounter.web.client.externalization.AccounterMessages;
import com.vimukti.accounter.web.client.i18n.AccounterNumberFormat;
import com.vimukti.accounter.web.client.util.DayAndMonthUtil;
import com.vimukti.accounter.web.server.i18n.ServerSideMessages;

public class ServerGlobal extends AbstractGlobal {

	AccounterMessages messages = ServerSideMessages
			.get(AccounterMessages.class);

	public ServerGlobal() throws IOException {
	}

	@Override
	public ClientCompanyPreferences preferences() {
		return CompanyPreferenceThreadLocal.get();
	}

	@Override
	public AccounterMessages messages() {
		return messages;
	}

	@Override
	public AccounterNumberFormat getFormater() {
		AccounterNumberFormat accounterNumberFormat = ServerNumberFormatThred
				.get();
		if (accounterNumberFormat == null) {
			accounterNumberFormat = new AccounterNumberFormat(preferences()
					.getCurrencyFormat(), preferences().getDecimalNumber(),
					true, preferences().getDecimalCharacter().charAt(0),
					preferences().getDigitGroupCharacter().charAt(0));
			ServerNumberFormatThred.set(accounterNumberFormat);
		}
		return accounterNumberFormat;
	}

	@Override
	public DayAndMonthUtil getDayAndMonthUtil() {
		return LocalInfoCache.get(ServerLocal.get());
	}
}
