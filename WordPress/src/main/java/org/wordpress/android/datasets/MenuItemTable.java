package org.wordpress.android.datasets;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.wordpress.android.WordPress;
import org.wordpress.android.models.MenuItemModel;

import java.util.ArrayList;
import java.util.List;

import static org.wordpress.android.util.SqlUtils.*;

/**
 */
public class MenuItemTable {
    //
    // Menu Item database table column names
    //
    /** SQL type - INTEGER (PRIMARY KEY) */
    public static final String ID_COLUMN = "itemId";
    /** SQL type - INTEGER */
    public static final String MENU_ID_COLUMN = "itemMenu";
    /** SQL type - INTEGER */
    public static final String PARENT_ID_COLUMN = "itemParent";
    /** SQL type - INTEGER */
    public static final String CONTENT_ID_COLUMN = "itemContentId";
    /** SQL type - TEXT */
    public static final String URL_COLUMN = "itemUrl";
    /** SQL type - TEXT */
    public static final String NAME_COLUMN = "itemName";
    /** SQL type - TEXT */
    public static final String DETAILS_COLUMN = "itemDetails";
    /** SQL type - TEXT */
    public static final String LINK_TARGET_COLUMN = "itemLinkTarget";
    /** SQL type - TEXT */
    public static final String LINK_TITLE_COLUMN = "itemLinkTitle";
    /** SQL type - TEXT */
    public static final String TYPE_COLUMN = "itemType";
    /** SQL type - TEXT */
    public static final String TYPE_FAMILY_COLUMN = "itemTypeFamily";
    /** SQL type - TEXT */
    public static final String TYPE_LABEL_COLUMN = "itemTypeLabel";
    /** SQL type - TEXT */
    public static final String CHILDREN_COLUMN = "itemChildren";

    //
    // Convenience SQL Strings
    //
    public static final String MENU_ITEMS_TABLE_NAME = "menu_items";

    public static final String CREATE_MENU_ITEMS_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " +
                    MENU_ITEMS_TABLE_NAME + " (" +
                    ID_COLUMN + " INTEGER PRIMARY KEY, " +
                    MENU_ID_COLUMN + " INTEGER, " +
                    PARENT_ID_COLUMN + " INTEGER, " +
                    CONTENT_ID_COLUMN + " INTEGER, " +
                    URL_COLUMN + " TEXT, " +
                    NAME_COLUMN + " TEXT, " +
                    DETAILS_COLUMN + " TEXT, " +
                    LINK_TARGET_COLUMN + " TEXT, " +
                    LINK_TITLE_COLUMN + " TEXT, " +
                    TYPE_COLUMN + " TEXT, " +
                    TYPE_FAMILY_COLUMN + " TEXT, " +
                    TYPE_LABEL_COLUMN + " TEXT, " +
                    CHILDREN_COLUMN + " TEXT" +
                    ");";

    /** SQL query to drop the Menu Items table */
    public static final String DROP_MENU_ITEMS_TABLE_SQL =
            "DROP TABLE " + MENU_ITEMS_TABLE_NAME + ";";

    /** Well-formed WHERE clause for identifying a row using PRIMARY KEY constraints */
    public static final String UNIQUE_WHERE_SQL = "WHERE " + ID_COLUMN + "=?";

    public static void saveMenuItem(MenuItemModel item) {
        if (item == null || item.itemId < 0) return;

        ContentValues row = serializeToDatabase(item);
        WordPress.wpDB.getDatabase().insertWithOnConflict(
                MENU_ITEMS_TABLE_NAME, null, row, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static void deleteMenuItem(long itemId) {
        if (itemId < 0) return;
        String[] args = {String.valueOf(itemId)};
        WordPress.wpDB.getDatabase().delete(MENU_ITEMS_TABLE_NAME, UNIQUE_WHERE_SQL, args);
    }

    public static void deleteAllMenuItems() {
        WordPress.wpDB.getDatabase().delete(MENU_ITEMS_TABLE_NAME, null, null);
    }

    public static MenuItemModel getMenuItem(long itemId) {
        if (itemId < 0) return null;

        String[] args = {String.valueOf(itemId)};
        Cursor cursor = WordPress.wpDB.getDatabase().rawQuery(UNIQUE_WHERE_SQL, args);
        cursor.moveToFirst();
        MenuItemModel item = deserializeFromDatabase(cursor);
        cursor.close();

        return item;
    }

    public static List<MenuItemModel> getAllMenuItems() {
        List<MenuItemModel> items = new ArrayList<>();
        Cursor cursor = WordPress.wpDB.getDatabase().rawQuery("SELECT * FROM " + MENU_ITEMS_TABLE_NAME + ";", null);
        if (cursor.moveToFirst()) {
            do {
                MenuItemModel item = deserializeFromDatabase(cursor);
                if (item != null) items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return items;
    }

    /**
     */
    public static MenuItemModel deserializeFromDatabase(Cursor cursor) {
        if (cursor == null || cursor.isBeforeFirst() || cursor.isAfterLast()) return null;

        MenuItemModel item = new MenuItemModel();
        item.itemId = getLongFromCursor(cursor, ID_COLUMN);
        item.menuId = getLongFromCursor(cursor, MENU_ID_COLUMN);
        item.parentId = getLongFromCursor(cursor, PARENT_ID_COLUMN);
        item.contentId = getLongFromCursor(cursor, CONTENT_ID_COLUMN);
        item.url = getStringFromCursor(cursor, URL_COLUMN);
        item.name = getStringFromCursor(cursor, NAME_COLUMN);
        item.details = getStringFromCursor(cursor, DETAILS_COLUMN);
        item.linkTarget = getStringFromCursor(cursor, LINK_TARGET_COLUMN);
        item.linkTitle = getStringFromCursor(cursor, LINK_TITLE_COLUMN);
        item.type = getStringFromCursor(cursor, TYPE_COLUMN);
        item.typeFamily = getStringFromCursor(cursor, TYPE_FAMILY_COLUMN);
        item.typeLabel = getStringFromCursor(cursor, TYPE_LABEL_COLUMN);
        item.setChildrenFromStringList(getStringFromCursor(cursor, CHILDREN_COLUMN));
    }

    /**
     */
    public static ContentValues serializeToDatabase(MenuItemModel item) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, item.itemId);
        values.put(MENU_ID_COLUMN, item.menuId);
        values.put(PARENT_ID_COLUMN, item.parentId);
        values.put(CONTENT_ID_COLUMN, item.contentId);
        values.put(URL_COLUMN, item.url);
        values.put(NAME_COLUMN, item.name);
        values.put(DETAILS_COLUMN, item.details);
        values.put(LINK_TARGET_COLUMN, item.linkTarget);
        values.put(LINK_TITLE_COLUMN, item.linkTitle);
        values.put(TYPE_COLUMN, item.type);
        values.put(TYPE_FAMILY_COLUMN, item.typeFamily);
        values.put(TYPE_LABEL_COLUMN, item.typeLabel);
        values.put(CHILDREN_COLUMN, separatedStringList(item.children, ","));
        return values;
    }
}
