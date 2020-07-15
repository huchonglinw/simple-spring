package com.huchonglin.util;

/**
 * @author: hcl
 * @date: 2020/7/5 14:59
 */
public class DbUtil {
    private static final String SQL_MARK = "?";
    /**
     * select * from user where username=? and password=?
     * 1.先根据空格切割
     * 2.
     *
     * @param sqlStatement
     * @return 新的sql语句 或者 null代表不需要解析
     */
    public String resolveSqlstatement(String sqlStatement) {
        if (sqlStatement == null || sqlStatement.equals("") || ! sqlStatement.contains(SQL_MARK)) {
            return null;
        }

        StringBuilder sql = new StringBuilder();
        String[] splits = sqlStatement.split(" ");
        for (String split : splits) {
            if(split.contains(SQL_MARK)) {

            }
            sql.append(split);
        }
        return null;
    }
}
