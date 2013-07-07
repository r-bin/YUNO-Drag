package org.yuno.apps.dragdropmanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.yuno.apps.dragdropmanager.helper.DropZone;
import org.yuno.apps.dragdropmanager.helper.SimpleWindowCallback;
import org.yuno.apps.dragdropmanager.helper.popup.PopupWindow;
import org.yuno.apps.dragdropmanager.support.DragEvent;
import org.yuno.apps.dragdropmanager.support.DragShadowBuilder;
import org.yuno.apps.dragdropmanager.support.raw.ClipData;
import org.yuno.apps.dragdropmanager.support.raw.ClipDescription;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class DragDropContainer {
	private final Activity m_activity;
	private final Map<View, DropZone> m_defaultDropzone;
	private final Callbacks m_callback;

	private final android.view.Window.Callback m_callbackDefault;
	private final SimpleWindowCallback m_simpleWindowCallback = createSimpleWindowCallback();

	private PopupView m_popup;
	private DragHolder m_holder;

	public interface Callbacks {
		boolean onDrag(View view, DragEvent event);
	}

	// EVENT

	public DragDropContainer(Activity activity, Map<View, DropZone> dropZone,
			Callbacks callback) {
		m_activity = activity;
		m_callbackDefault = m_activity.getWindow().getCallback();

		m_defaultDropzone = dropZone;
		m_callback = callback;
	}

	// PUBLIC

	public synchronized void startDrag(View view, ClipData data,
			DragShadowBuilder shadowBuilder, Object myLocalState, int flags) {
		if (m_holder == null) {
			m_holder = createHolder(view, data, shadowBuilder, myLocalState,
					flags);

			startDrag();
		}
	}

	// PRIVATE

	private void lock(boolean lock) {
		// TODO: reenable keys while lock

		lockInput(lock);
		lockSensor(lock);
	}

	private void lockInput(boolean lock) {
		if (lock) {
			m_activity.getWindow().setCallback(m_simpleWindowCallback);
		} else {
			m_activity.getWindow().setCallback(m_callbackDefault);
		}
	}

	private void lockSensor(boolean lock) {
		if (lock) {
			m_activity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		} else {
			m_activity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
	}

	private void startDrag() {
		if (m_holder != null) {
			lock(true);

			m_popup = new PopupWindow(m_activity, m_holder.shadow,
					m_holder.view);

			fakeMotionEvent(MotionEvent.ACTION_DOWN, m_holder.view);
		}
	}

	private void stopDrag() {
		if (m_holder != null) {
			m_popup.dismiss();

			m_holder = null;

			lock(false);
		}
	}

	private void updateDrag(int x, int y) {
		int pos_x = x + (-m_popup.getWidth() / 2);
		int pos_y = y + (-m_popup.getHeight() / 2);

		m_popup.showAtLocation(pos_x, pos_y);
	};

	private boolean isEventInView(View view, MotionEvent event) {
		Rect rect = new Rect();
		int[] location = new int[2];

		view.getLocationInWindow(location);
		view.getDrawingRect(rect);
		rect.offset(location[0], location[1]);

		return rect.contains((int) event.getRawX(), (int) event.getRawY());
	}

	private void checkDropZones(MotionEvent event) {
		// TODO: should return hovered dropzone based on z-axis and
		// touch-position instead to prevent multiselections

		for (DropZone dz : m_holder.dropzone.values()) {
			// TODO: find focused view by comparing z-index of siblings

			boolean isOver = isEventInView(dz.view, event);
			int action = DragEvent.ACTION_INVALID;

			if (event.getAction() == MotionEvent.ACTION_UP) {
				dz.isOver = false;

				if (isOver) {
					action = DragEvent.ACTION_DROP;
				}
			} else if (isOver != dz.isOver) {
				dz.isOver = isOver;

				if (isOver) {
					action = DragEvent.ACTION_DRAG_ENTERED;
				} else {
					action = DragEvent.ACTION_DRAG_EXITED;
				}
			} else if (isOver && event.getAction() == MotionEvent.ACTION_MOVE) {
				if (DragDropManager.SUPPORT_ACTION_DRAG_LOCATION) {
					action = DragEvent.ACTION_DRAG_LOCATION;
				}
			}

			if (action != DragEvent.ACTION_INVALID) {
				fakeDragEvent(action, event, dz.view);
			} else {
				// if (BuildConfig.DEBUG) {
				// Log.d(DragDropManager.TAG, "ignored invalid action '"
				// + action + "'");
				// }
			}
		}
	}

	// FAKE EVENTS

	private void removeDropZone(View dropZone) {
		m_holder.dropzone.remove(dropZone);
	}

	private void handleDragResult(View view, DragEvent supportEvent) {
		boolean ret = m_callback.onDrag(view, supportEvent);

		if (supportEvent.getAction() == DragEvent.ACTION_DRAG_STARTED) {
			if (!ret) {
				removeDropZone(view);
			}
		} else if (supportEvent.getAction() == DragEvent.ACTION_DROP) {
			m_holder.result = ret;
		}
	}

	private void fakeDragEvent(int action, MotionEvent event, View view) {
		int[] location = new int[2];
		view.getLocationInWindow(location);

		DragEvent supportEvent = new DragEvent(action, event.getX()
				- location[0], event.getY() - location[1], m_holder.localState,
				m_holder.description, m_holder.data, m_holder.result);

		handleDragResult(view, supportEvent);
	}

	private void fakeDragEvent(int action, MotionEvent event) {
		DragEvent supportEvent = new DragEvent(action, event.getX(),
				event.getY(), m_holder.localState, m_holder.description,
				m_holder.data, m_holder.result);

		for (Iterator<Entry<View, DropZone>> it = m_holder.dropzone.entrySet()
				.iterator(); it.hasNext();) {
			Entry<View, DropZone> dz = it.next();

			handleDragResult(dz.getValue().view, supportEvent);
		}
	}

	private void fakeMotionEvent(int event, View view) {
		int[] location = new int[2];
		view.getLocationInWindow(location);

		MotionEvent motionEvent = MotionEvent.obtain(0, 0, event, location[0]
				+ view.getWidth() / 2, location[1] + view.getHeight() / 2, 0,
				0, 0, 0, 0, 0, 0);

		m_activity.getWindow().getCallback().dispatchTouchEvent(motionEvent);

		motionEvent.recycle();
	}

	private void fakeMotionEvent(MotionEvent motionEvent) {
		m_activity.getWindow().getCallback().dispatchTouchEvent(motionEvent);
	}

	// FACTORY

	private SimpleWindowCallback createSimpleWindowCallback() {
		return new SimpleWindowCallback() {
			public boolean dispatchTouchEvent(MotionEvent event) {
				checkDropZones(event);

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					fakeDragEvent(DragEvent.ACTION_DRAG_STARTED, event);
				}

				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					updateDrag((int) event.getRawX(), (int) event.getRawY());
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					fakeDragEvent(DragEvent.ACTION_DRAG_ENDED, event);

					stopDrag();

					fakeMotionEvent(event);
				}

				return true;
			}
		};
	}

	private DragHolder createHolder(View view, ClipData data,
			DragShadowBuilder shadowBuilder, Object myLocalState, int flags) {
		DragHolder holder = new DragHolder();

		holder.view = view;

		holder.data = data;
		holder.shadowBuilder = shadowBuilder;
		holder.shadow = shadowBuilder.supportCreateShadowView(m_activity);
		holder.localState = myLocalState;

		holder.flags = flags;

		holder.dropzone.putAll(m_defaultDropzone);

		return holder;
	}

	// CLASS

	private class DragHolder {
		Map<View, DropZone> dropzone = new HashMap<View, DropZone>();

		View view;
		View shadow;
		@SuppressWarnings("unused")
		int flags; // TODO

		ClipData data;
		ClipDescription description;
		@SuppressWarnings("unused")
		DragShadowBuilder shadowBuilder;
		Object localState;

		boolean result;
	}
}