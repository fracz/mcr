package pl.fracz.mcr.fragment;

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

    public void displaySourceFile(SourceFile sourceFile) {
        contents.removeAllViews();
        for (Line line : sourceFile.getLines(getActivity()))
            contents.addView(line);
    }
}
