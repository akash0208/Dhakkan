package com.shopby.dhakkan.network.parser;
import com.shopby.dhakkan.model.Category;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/13/2016.
 */
public class CategoryParser {

    public ArrayList<Category> getCategory(String response) {
        if (response != null) {
            try {

                ArrayList<Category> categoryList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int id = 0;
                    String name = null, image = null, description = null;

                    if (jsonObject.has(ParserKey.KEY_ID)) {
                        id = jsonObject.getInt(ParserKey.KEY_ID);
                    }
                    if (jsonObject.has(ParserKey.KEY_NAME)) {
                        name = jsonObject.getString(ParserKey.KEY_NAME);
                    }
                    if (jsonObject.has(ParserKey.KEY_DESCRIPTION)) {
                        description = jsonObject.getString(ParserKey.KEY_DESCRIPTION);
                    }

                    if (jsonObject.has(ParserKey.KEY_IMAGE)) {
                        try {
                            JSONObject imageJson = jsonObject.getJSONObject(ParserKey.KEY_IMAGE);
                            if (imageJson.has(ParserKey.KEY_IMAGE_SOURCE)) {
                                image = imageJson.getString(ParserKey.KEY_IMAGE_SOURCE);
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }

                    categoryList.add(new Category(id, name, image, description, false));

                }
                return categoryList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
