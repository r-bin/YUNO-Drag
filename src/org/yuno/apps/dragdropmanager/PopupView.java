package org.yuno.apps.dragdropmanager;

import android.app.Activity;
import android.view.View;

public abstract class PopupView {
	protected Activity m_activity;

	// EVENT

	public PopupView(Activity activity, View contentView, View anchorView) {
		m_activity = activity;
	}

	// PUBLIC

	public void showAtLocation(int x, int y) {
	}

	public void dismiss() {
	}

	// GET

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}
}
