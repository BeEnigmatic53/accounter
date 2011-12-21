package com.vimukti.accounter.mobile.requirements;

import java.util.List;

import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.InputType;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.ResultList;

public abstract class CommandsRequirement extends AbstractRequirement<String> {

	public CommandsRequirement(String requirementName) {
		super(requirementName, null, null, true, false);
	}

	@Override
	public Result run(Context context, Result makeResult, ResultList list,
			ResultList actions) {
		boolean canAddToResult = canAddToResult();
		String value = null;
		if (canAddToResult) {
			value = context.getSelection(getName());
		} else {
			value = context.getSelection(ACTIONS);
		}
		Object attribute = context.getAttribute("onSelection");
		context.removeAttribute("onSelection");
		List<String> actionNames = getList();
		if (attribute == null && actionNames.contains(value)) {
			setValue(value);
			String onSelection = onSelection(value);
			if (onSelection != null) {
				context.setAttribute("onSelection", "");
				Result result = new Result();
				result.setNextCommand(onSelection);
				context.getIOSession().getCurrentCommand()
						.setCanTrackRequirements(false);
				return result;
			}
			context.setString("");
		}
		ResultList resultList = new ResultList(getName());
		if (canAddToResult) {
			makeResult.add(resultList);
		} else {
			resultList = actions;
		}

		for (String n : actionNames) {
			Record record = new Record(n);
			record.add(n);
			resultList.add(record);
		}

		return null;
	}

	protected boolean canAddToResult() {
		return true;
	}

	public String onSelection(String value) {
		return null;
	}

	protected abstract List<String> getList();

	@Override
	public InputType getInputType() {
		return new InputType(INPUT_TYPE_NONE);
	}
}
