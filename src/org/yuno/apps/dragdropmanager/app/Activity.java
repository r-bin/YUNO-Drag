package org.yuno.apps.dragdropmanager.app;

import org.yuno.apps.dragdropmanager.DragDropManager;

import android.os.Bundle;

public class Activity extends android.app.Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DragDropManager.createInstance(this);

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		DragDropManager.destroyInstance();

		super.onDestroy();
	}
}
