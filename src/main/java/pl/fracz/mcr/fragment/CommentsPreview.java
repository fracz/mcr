package pl.fracz.mcr.fragment;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockListFragment;
import com.googlecode.androidannotations.annotations.EFragment;
import pl.fracz.mcr.R;
import pl.fracz.mcr.comment.CommentsListAdapter;
import pl.fracz.mcr.source.Line;

@EFragment(R.layout.comments_preview)
public class CommentsPreview extends SherlockListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void displayComments(Line line) {
        setListAdapter(new CommentsListAdapter(getActivity(), line));
    }
}
