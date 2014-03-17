package pl.fracz.mcr.fragment;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

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
            Line line = (Line) v;
            if (line.getComments().isEmpty()) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.line_has_no_comments), Toast.LENGTH_SHORT).show();
            } else {
                FrameLayout comments = (FrameLayout) getActivity().findViewById(R.id.commentsPreviewContainer);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) comments.getLayoutParams();
                params.weight = 1;
                comments.setLayoutParams(params);
            }
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
