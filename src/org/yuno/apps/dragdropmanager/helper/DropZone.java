package org.yuno.apps.dragdropmanager.helper;

import org.yuno.apps.dragdropmanager.support.OnDragListener;

import android.view.View;

public class DropZone {
	public final View view;
	public final OnDragListener listener;

	public boolean isOver = false;

	// EVENT

	public DropZone(View dropZone, OnDragListener onSupportDragListener) {
		view = dropZone;
		listener = onSupportDragListener;
	}
}