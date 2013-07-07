package org.yuno.apps.dragdropmanager.support;

import org.yuno.apps.dragdropmanager.BuildConfig;
import org.yuno.apps.dragdropmanager.DragDropManager;
import org.yuno.apps.dragdropmanager.support.raw.ClipData;
import org.yuno.apps.dragdropmanager.support.raw.ClipDescription;

import android.annotation.SuppressLint;
import android.util.Log;

public class DragEvent {
	public static final int ACTION_INVALID = 0;

	public static final int ACTION_DRAG_STARTED = 1;
	public static final int ACTION_DRAG_LOCATION = 2;
	public static final int ACTION_DROP = 3;
	public static final int ACTION_DRAG_ENDED = 4;
	public static final int ACTION_DRAG_ENTERED = 5;
	public static final int ACTION_DRAG_EXITED = 6;

	private int mAction = ACTION_INVALID;

	private float mX = 0f;
	private float mY = 0f;

	private Object mLocalState = null;
	private ClipData mClipData = null;
	private ClipDescription mClipDescription = null;

	private boolean mDragResult = false;

	// EVENT

	public DragEvent() {
		this(0, 0f, 0f, null, null, null, false);
	}

	@SuppressLint("NewApi")
	public DragEvent(android.view.DragEvent event) {
		this(event.getAction(), event.getX(), event.getY(), event
				.getLocalState(), convertClipDescription(event
				.getClipDescription()), convertClipData(event.getClipData()),
				event.getResult());
	}

	public DragEvent(int action, float x, float y, Object localState,
			ClipDescription description, ClipData data, boolean result) {
		setAction(action);

		setX(x);
		setY(y);

		setLocalState(localState);
		setClipDescription(description);
		setClipData(data);

		setResult(result);
	}

	// SET

	private void setX(float x) {
		if (mAction == ACTION_DRAG_STARTED || mAction == ACTION_DRAG_LOCATION
				|| mAction == ACTION_DROP) {
			mX = x;
		}
	}

	private void setY(float y) {
		if (mAction == ACTION_DRAG_STARTED || mAction == ACTION_DRAG_LOCATION
				|| mAction == ACTION_DROP) {
			mY = y;
		}
	}

	private void setAction(int action) {
		mAction = action;

		if (mAction == ACTION_INVALID) {
			if (BuildConfig.DEBUG) {
				Log.e(DragDropManager.TAG,
						"Invalid SupportDragEvent created (action = '" + action
								+ "')");
			}
		}
	}

	private void setClipDescription(ClipDescription description) {
		if (mAction != ACTION_INVALID) {
			mClipDescription = description;
		}
	}

	private void setClipData(ClipData data) {
		if (mAction == ACTION_DROP) {
			mClipData = data;
		}
	}

	private void setLocalState(Object localState) {
		if (mAction != ACTION_INVALID) {
			mLocalState = localState;
		}
	}

	private void setResult(boolean result) {
		if (mAction == ACTION_DROP) {
			mDragResult = result;
		}
	}

	// GET

	public float getX() {
		return mX;
	}

	public float getY() {
		return mY;
	}

	public int getAction() {
		return mAction;
	}

	public ClipDescription getClipDescription() {
		return mClipDescription;
	}

	public ClipData getClipData() {
		return mClipData;
	}

	public Object getLocalState() {
		return mLocalState;
	}

	public boolean getResult() {
		return mDragResult;
	}

	// SUPPORT

	@SuppressLint("NewApi")
	private static ClipDescription convertClipDescription(
			android.content.ClipDescription description) { // TODO: remove
		// TODO: convert once
		if (DragDropManager.SUPPORT_CLIP) {
			return new ClipDescription(description);
		} else {
			return null;
		}
	}

	@SuppressLint("NewApi")
	private static ClipData convertClipData(android.content.ClipData data) { // TODO:
																				// remove
		// TODO: convert once
		if (DragDropManager.SUPPORT_CLIP) {
			return new ClipData(data);
		} else {
			return null;
		}
	}
}