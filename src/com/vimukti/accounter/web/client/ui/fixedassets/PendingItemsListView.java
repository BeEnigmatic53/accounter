/**
 * 
 */
package com.vimukti.accounter.web.client.ui.fixedassets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vimukti.accounter.web.client.core.ClientFixedAsset;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.ActionFactory;
import com.vimukti.accounter.web.client.ui.core.BaseListView;
import com.vimukti.accounter.web.client.ui.grids.PendingItemsListGrid;

/**
 * @author Murali.A
 * 
 */
public class PendingItemsListView extends BaseListView<ClientFixedAsset> {

	public PendingItemsListView() {
		this.isViewSelectRequired = false;
	}

	@Override
	protected Action getAddNewAction() {
		return ActionFactory.getNewFixedAssetAction();
	}

	@Override
	protected String getAddNewLabelString() {
		return messages().addNewAsset();
	}

	@Override
	protected String getListViewHeading() {
		return messages().pendingItemsList();
	}

	@Override
	protected void initGrid() {
		grid = new PendingItemsListGrid(false);
		grid.init();
		getPendingFixedAssetsList();
		// grid.setRecords(getAssetsByType(ClientFixedAsset.STATUS_PENDING,
		// getCompany().getFixedAssets()));
		disableFilter();
	}

	private void getPendingFixedAssetsList() {
		Accounter.createHomeService().getFixedAssetList(
				ClientFixedAsset.STATUS_PENDING,
				new AsyncCallback<List<ClientFixedAsset>>() {

					@Override
					public void onSuccess(List<ClientFixedAsset> list) {
						grid.setRecords(list);
						grid.sort(10, false);
					}

					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

	@Override
	public void initListCallback() {
		// FinanceApplication.createGETService().getObjects(
		// AccounterCoreType.FIXEDASSET, this);

	}

	@Override
	public void updateInGrid(ClientFixedAsset objectTobeModified) {
	}

	@Override
	public void fitToSize(int height, int width) {
		super.fitToSize(height, width);

	}

	@Override
	public void onEdit() {
		// Nothing to do

	}

	@Override
	public void print() {
		// NOTHING to do

	}

	@Override
	public void printPreview() {
		// NOTHING TO DO.
	}

	@Override
	public Map<String, Object> saveView() {
		Map<String, Object> map = new HashMap<String, Object>();
		// map.put("isActive", isActiveAccounts);
		map.put("start", start);
		return map;
	}

	@Override
	public void restoreView(Map<String, Object> viewDate) {

		if (viewDate == null || viewDate.isEmpty()) {
			return;
		}
		// isActiveAccounts = (Boolean) viewDate.get("isActive");
		start = (Integer) viewDate.get("start");
		onPageChange(start, getPageSize());
		// if (isActiveAccounts) {
		// viewSelect.setComboItem(messages().active());
		// } else {
		// viewSelect.setComboItem(messages().inActive());
		// }

	}

	@Override
	protected String getViewTitle() {
		return messages().pendingItemsList();
	}

}
