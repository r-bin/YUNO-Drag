package org.yuno.apps.dragdropmanager.sample;

import org.yuno.apps.dragdropmanager.DragDropManager;
import org.yuno.apps.dragdropmanager.R;
import org.yuno.apps.dragdropmanager.app.Activity;
import org.yuno.apps.dragdropmanager.support.DragEvent;
import org.yuno.apps.dragdropmanager.support.DragShadowBuilder;
import org.yuno.apps.dragdropmanager.support.OnDragListener;
import org.yuno.apps.dragdropmanager.support.raw.ClipData;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SampleActivity extends Activity {
	// TODO: recursive call startDrag() in onDrag()
	// TODO: call startDrag() on MotionEvent.ACTION_UP

	// EVENT

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button b1 = (Button) findViewById(R.id.button1);
		Button b2 = (Button) findViewById(R.id.button2);

		EditText et1 = (EditText) findViewById(R.id.editText1);
		TextView tv1 = (TextView) findViewById(R.id.textView1);

		Button b3 = (Button) findViewById(R.id.button3);
		Button b4 = (Button) findViewById(R.id.button4);

		b1.setOnTouchListener(new MyTouchListener());
		b2.setOnLongClickListener(new MyLongTouchListener());

		tv1.setOnTouchListener(new MyTouchListener());

		DragDropManager.getInstance().setOnDragListener(b3,
				new MyDragToastListener());
		DragDropManager.getInstance().setOnDragListener(b4,
				new MyDragToastListener());
		DragDropManager.getInstance().setOnDragListener(et1,
				new MyDragTextListener());
	}

	// PRIVATE

	private void startDrag(View view) {
		// Create a new ClipData.Item from the ImageView object's tag
		ClipData.Item item = new ClipData.Item("test");

		// Create a new ClipData using the tag as a label, the plain text MIME
		// type, and
		// the already-created item. This will create a new ClipDescription
		// object within the
		// ClipData, and set its MIME type entry to "text/plain"
		// ClipData dragData = new
		// ClipData("test",ClipDescription.MIMETYPE_TEXT_PLAIN,item);
		ClipData dragData = new ClipData("test", new String[] { "test" }, item);

		DragShadowBuilder shadowBuilder = new DragShadowBuilder(view);
		DragDropManager.getInstance().startDrag(view, dragData, shadowBuilder,
				view, 0);
	}

	// CLASS

	private final class MyTouchListener implements OnTouchListener {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				startDrag(view);

				return true;
			} else {
				return false;
			}
		}
	}

	@SuppressWarnings("unused")
	private final class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			startDrag(v);
		}
	}

	private final class MyLongTouchListener implements OnLongClickListener {
		@Override
		public boolean onLongClick(View v) {
			startDrag(v);

			return true;
		}
	}

	class MyDragToastListener implements OnDragListener {
		private Toast mToast;

		protected String name_dropZone_dump;

		// PUBLIC

		@Override
		public boolean onDrag(View v, DragEvent event) {
			int action = event.getAction();

			// TODO: validate data via mime-type provided by
			// "event.getClipDescription()"
			// TODO: "return false" if mime-type is not supported

			View view = (View) event.getLocalState();
			// ViewGroup owner = (ViewGroup) view.getParent();

			String name_dropZone = name_dropZone_dump;
			String name_dragView = getText(view);

			switch (action) {
			case DragEvent.ACTION_DRAG_STARTED:
				name_dropZone_dump = name_dropZone = getText(v);

				view.setVisibility(View.INVISIBLE);

				break; // TODO: return true or false to accept or decline events
						// for data based on clipdescription
			case DragEvent.ACTION_DRAG_ENTERED:
				if (v instanceof Button && view instanceof Button) {
					setText(v, "valid!");
				} else {
					setText(v, "invalid.");
				}

				break;
			case DragEvent.ACTION_DRAG_LOCATION:
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				setText(v, name_dropZone_dump);

				break;
			case DragEvent.ACTION_DROP:
				// ClipData.Item item = event.getClipData().getItemAt(0);

				String msg;

				if (v instanceof Button && view instanceof Button) {
					msg = "[" + name_dragView + "] → [" + name_dropZone + "]";
				} else {
					msg = "[" + name_dragView + "] x [" + name_dropZone + "]";
				}

				showToast(msg);

				// Dropped, reassign View to ViewGroup
				// owner.removeView(view);
				// LinearLayout container = (LinearLayout) v;
				// container.addView(view);

				break; // TODO: "return true" for valid drops
			case DragEvent.ACTION_DRAG_ENDED:
				setText(v, name_dropZone_dump);

				view.setVisibility(View.VISIBLE);
			default:
				break;
			}
			return true;
		}

		// PRIVATE

		protected String getText(View v) {
			String text = "unknown";

			if (v instanceof TextView) {
				text = (String) ((TextView) v).getText().toString();
			} else {
				// TODO
			}

			return text;
		}

		protected void setText(View v, String text) {
			if (v instanceof TextView) {
				((TextView) v).setText(text);
			} else {
				// TODO
			}
		}

		private void showToast(String msg) {
			if (mToast != null) {
				mToast.cancel();
				mToast = null;
			}

			mToast = Toast.makeText(SampleActivity.this, msg,
					Toast.LENGTH_SHORT);
			mToast.show();
		}
	}

	class MyDragTextListener extends MyDragToastListener {
		// PUBLIC

		@Override
		public boolean onDrag(View v, DragEvent event) {
			int action = event.getAction();

			View view = (View) event.getLocalState();

			switch (action) {
			case DragEvent.ACTION_DRAG_STARTED:
				name_dropZone_dump = getText(v);

				view.setVisibility(View.INVISIBLE);

				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				if (v instanceof EditText && view instanceof TextView) {
					setText(v, "valid!");
				} else {
					setText(v, "invalid.");
				}

				break;
			case DragEvent.ACTION_DRAG_LOCATION:
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				setText(v, name_dropZone_dump);

				break;
			case DragEvent.ACTION_DROP:
				if (v instanceof EditText && view instanceof TextView) {
					name_dropZone_dump = (String) ((TextView) view).getText();
				}

				break;
			case DragEvent.ACTION_DRAG_ENDED:
				setText(v, name_dropZone_dump);

				view.setVisibility(View.VISIBLE);
			default:
				break;
			}
			return true;
		}
	}
}
