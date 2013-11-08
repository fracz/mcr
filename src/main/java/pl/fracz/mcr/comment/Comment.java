package pl.fracz.mcr.comment;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

public abstract class Comment extends JSONObject {
    private static final String CREATION_TIME = "time";
    private static final String COMMENT_TYPE = "type";
    private static final String LINE_NUMBER = "line";
    private static final String AUTHOR = "author";

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

    void setCreationTime() {
        safePut(CREATION_TIME, System.currentTimeMillis());
    }

    void setLineNumber(int lineNumber) {
        safePut(LINE_NUMBER, lineNumber);
    }

    void setAuthor(String author) {
        safePut(AUTHOR, author);
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
