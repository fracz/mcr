package pl.fracz.mcr.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import pl.fracz.mcr.R;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Comment extends JSONObject {
    private static final String CREATION_TIME = "time";
    private static final String COMMENT_TYPE = "type";
    private static final String LINE_NUMBER = "line";
    private static final String AUTHOR = "author";

    private static final SimpleDateFormat CREATION_TIME_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");

    /**
     * Constructs the comment based on read JSON file.
     *
     * @param object json file
     * @throws JSONException
     */
    Comment(JSONObject object) throws JSONException {
        safePut(CREATION_TIME, object.get(CREATION_TIME));
        safePut(COMMENT_TYPE, object.get(COMMENT_TYPE));
        safePut(LINE_NUMBER, object.get(LINE_NUMBER));
        safePut(AUTHOR, object.get(AUTHOR));
    }

    public Comment() {
        safePut(COMMENT_TYPE, Type.getByClass(getClass()));
    }

    public long getCreationTime() {
        return Long.valueOf(safeGet(CREATION_TIME).toString());
    }

    public View getView(Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(getLayoutResourceId(), null);
        TextView author = (TextView) view.findViewById(R.id.author);
        if (author != null)
            author.setText(getAuthor());
        TextView time = (TextView) view.findViewById(R.id.time);
        if (time != null)
            time.setText(getCreationTimeFormatted());
        return view;
    }

    protected abstract int getLayoutResourceId();

    void setCreationTime() {
        safePut(CREATION_TIME, System.currentTimeMillis());
    }

    protected String getCreationTimeFormatted() {
        return CREATION_TIME_FORMAT.format(new Date(getCreationTime()));
    }

    void setLineNumber(int lineNumber) {
        safePut(LINE_NUMBER, lineNumber);
    }

    void setAuthor(String author) {
        safePut(AUTHOR, author);
    }

    protected String getAuthor() {
        return String.valueOf(safeGet(AUTHOR));
    }

    int getLineNumber() {
        return Integer.valueOf(safeGet(LINE_NUMBER).toString());
    }

    protected Object safeGet(String key) {
        try {
            return get(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected void safePut(String key, Object value) {
        try {
            put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    static Comment fromJSONObject(JSONObject commentObject) throws JSONException {
        String type = commentObject.getString(COMMENT_TYPE);
        Type commentType;
        try {
            commentType = Type.valueOf(type);
            return commentType.commentClass.getDeclaredConstructor(JSONObject.class).newInstance(commentObject);
        } catch (IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            throw new JSONException(e.getMessage());
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Comment objects must have the JSONObject constructor.");
        }
    }

    static enum Type {
        TEXT(TextComment.class);

        private final Class<? extends Comment> commentClass;

        private Type(Class<? extends Comment> comentClass) {
            this.commentClass = comentClass;
        }

        static Type getByClass(Class<? extends Comment> comentClass) {
            for (Type type : values()) {
                if (type.commentClass.equals(comentClass)) {
                    return type;
                }
            }
            throw new RuntimeException("Comment type could not be found.");
        }
    }
}
