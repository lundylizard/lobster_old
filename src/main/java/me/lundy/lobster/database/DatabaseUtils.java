package me.lundy.lobster.database;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtils {

    private static final Map<String, String> typeMap = new HashMap<>();

    static {
        typeMap.put("long", "BIGINT");
        typeMap.put("boolean", "BOOLEAN");
        typeMap.put("int", "INT");
        typeMap.put("String", "TEXT");
        typeMap.put("float", "FLOAT");
    }

    //public static void main(String[] args) {
    //    // SelectCriteriaBuilder selectCriteriaBuilder = new SelectCriteriaBuilder();
    //    // selectCriteriaBuilder.setFields("guildId");
    //    // selectCriteriaBuilder.setValues("0");
    //    System.out.println(generateSelectQuery(GuildSettings.class, selectCriteriaBuilder.none()));
    //    System.out.println(generateUpdateQuery(GuildSettings.class));
    //    System.out.println(generateUpdateQuery(UserSettings.class));
    //}

    public static String getTableNameFromClass(Class<?> type) {
        String tableName;
        if (type.isAnnotationPresent(Table.class)) {
            Table table = type.getAnnotation(Table.class);
            tableName = table.name();
        } else {
            throw new IllegalArgumentException("Provided class does not have Table annotation.");
        }
        return tableName;
    }

    public static String getSQLTypeFromField(Field field) {
        String fieldType = field.getType().getSimpleName();
        return typeMap.get(fieldType);
    }

    public static String generateSelectQuery(Class<?> type) {
        String tableName = DatabaseUtils.getTableNameFromClass(type);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ");

        for (Field field : type.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Id.class)) stringBuilder.append(field.getName()).append(", ");
        }

        int last = stringBuilder.length() - 1;
        stringBuilder.replace(last - 1, last + 1, " ");
        stringBuilder.append("FROM ").append(tableName).append(" ");
        //stringBuilder.append(selectCriteria.query());
        return stringBuilder.toString();
    }

    public static String generateCreateTableQuery(Class<?> type) {
        String tableName = DatabaseUtils.getTableNameFromClass(type);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE ").append(tableName).append("(");

        for (Field field : type.getDeclaredFields()) {
            String sqlType = getSQLTypeFromField(field);
            stringBuilder.append(field.getName()).append(" ").append(sqlType).append(" NOT NULL");
            if (field.isAnnotationPresent(Id.class)) stringBuilder.append(" PRIMARY KEY");
            stringBuilder.append(", ");
        }

        int last = stringBuilder.length() - 1;
        stringBuilder.replace(last - 1, last + 1, ")");
        return stringBuilder.toString();
    }

    public static String generateUpdateQuery(Class<?> type) {
        String tableName = DatabaseUtils.getTableNameFromClass(type);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE ").append(tableName).append(" SET ");

        String idField = "";
        boolean typeContainsId = false;
        for (Field field : type.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Id.class)) {
                stringBuilder.append(field.getName()).append(" = ?, ");
            } else {
                idField = field.getName();
                typeContainsId = true;
            }
        }

        if (!typeContainsId) throw new IllegalArgumentException("Provided class does not have a field annotated as Id");
        int last = stringBuilder.length() - 1;
        stringBuilder.replace(last - 1, last + 1, " WHERE ");
        stringBuilder.append(idField).append(" = ?");
        return stringBuilder.toString();
    }

}
