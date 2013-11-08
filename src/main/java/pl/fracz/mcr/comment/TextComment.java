package pl.fracz.mcr.comment;

import org.json.JSONException;
import org.json.JSONObject;

public class TextComment extends Comment {
    private static final String TEXT = "text";

    TextComment(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        safePut(TEXT, jsonObject.get(TEXT));
    }

    public TextComment(String text) {
        safePut(TEXT, text);
    }

    public String getText() {
        return String.valueOf(safeGet(TEXT));
    }
}
