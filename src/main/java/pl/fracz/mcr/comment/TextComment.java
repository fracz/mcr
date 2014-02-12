package pl.fracz.mcr.comment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import pl.fracz.mcr.R;
import pl.fracz.mcr.source.SourceFile;

public class TextComment extends Comment {
    private static final String TEXT = "text";

    TextComment(JSONObject jsonObject, SourceFile sourceFile) throws JSONException {
        super(jsonObject, sourceFile);
        safePut(TEXT, jsonObject.get(TEXT));
    }

    public TextComment(String text) {
        safePut(TEXT, text);
    }

    public String getText() {
        return String.valueOf(safeGet(TEXT));
    }

    @Override
    public View getView(Context context) {
        View view = super.getView(context);
        TextView comment = (TextView) view.findViewById(R.id.comment);
        comment.setText(getText());
        return view;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.comment_text;
    }
}
