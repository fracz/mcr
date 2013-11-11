package pl.fracz.mcr.comment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import pl.fracz.mcr.source.Line;

import java.util.List;

public class CommentsListAdapter extends BaseAdapter {

    private final Context context;

    private final List<Comment> comments;

    public CommentsListAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    public CommentsListAdapter(Context context, Line line) {
        this(context, line.getComments());
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0L;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment comment = comments.get(position);
        if (comment == null)
            throw new RuntimeException(String.format("Comment [%d] does not exist.", position));
        return comment.getView(context);
    }
}
