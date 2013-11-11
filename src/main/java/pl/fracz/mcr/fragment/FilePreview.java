package pl.fracz.mcr.fragment;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockFragment;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import pl.fracz.mcr.R;
import pl.fracz.mcr.source.Line;
import pl.fracz.mcr.source.SourceFile;

@EFragment(R.layout.file_preview)
public class FilePreview extends SherlockFragment {
    @ViewById
    LinearLayout contents;

    private final View.OnLongClickListener onLineLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            FrameLayout comments = (FrameLayout) getActivity().findViewById(R.id.commentsPreviewContainer);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) comments.getLayoutParams();
            params.weight = 1;
            comments.setLayoutParams(params);
            return false;
        }
    };

    public void displaySourceFile(SourceFile sourceFile) {
        contents.removeAllViews();
        for (Line line : sourceFile.getLines(getActivity())) {
            line.setLongClickable(true);
            line.setOnLongClickListener(onLineLongClick);
            contents.addView(line);
        }
    }
}
