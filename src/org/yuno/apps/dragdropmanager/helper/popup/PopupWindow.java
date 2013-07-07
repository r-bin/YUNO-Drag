package org.yuno.apps.dragdropmanager.helper.popup;

import org.yuno.apps.dragdropmanager.PopupView;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class PopupWindow extends PopupView {
	private android.widget.PopupWindow mPopoUp;

	// EVENT

	public PopupWindow(Activity activity, View contentView, View anchorView) {
		super(activity, contentView, anchorView);

		// TODO: allow the popup to leave the window partially
		// TODO: add transparency to popup

		mPopoUp = new android.widget.PopupWindow(contentView,
				anchorView.getWidth(), anchorView.getHeight());
		mPopoUp.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		int[] location = new int[2];
		anchorView.getLocationInWindow(location);

		showAtLocation(location[0], location[1]);
	}

	// PUBLIC

	@Override
	public void showAtLocation(int x, int y) {
		if (!mPopoUp.isShowing()) {
			mPopoUp.showAtLocation(m_activity.getWindow().getDecorView(),
					Gravity.NO_GRAVITY, x, y);
		} else {
			mPopoUp.update(x, y, -1, -1);
		}
	}

	@Override
	public void dismiss() {
		mPopoUp.dismiss();
	}

	// GET

	@Override
	public int getWidth() {
		return mPopoUp.getWidth();
	}

	@Override
	public int getHeight() {
		return mPopoUp.getHeight();
	}
}
