package com.hybris.easyjet.database;

import javafx.util.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enum class to store the key related to the function of sql server grammar
 * For each key can be associated one or more function with related regex identification
 */
enum SQLFunction {
    INTERVAL,
    CURRENT_TIME,
    LIMIT_RESULT,
    CONVERT_DATE
}

/**
 * Created by Giuseppe on 20/01/2017.
 */
public class QueryParser {


    /**
     * The main goal for the class if to parse simple query based on sql, in order to run it with both sqlserver and mysql driver
     * We assume the only scope of the driver for mysql for local environment
     * We assume that the environment local should be mysql as database
     */
    private static final String ENVIRONMENT = System.getProperty("environment");
    private static final Map<SQLFunction, Pair> functionSQLServer = new EnumMap<SQLFunction, Pair>(SQLFunction.class);
    private static final Map<SQLFunction, Pair> functionMySQL = new EnumMap<SQLFunction, Pair>(SQLFunction.class);

    static {
        functionSQLServer.put(SQLFunction.CURRENT_TIME, new Pair<>(" GETDATE() ", "(?i)getdate\\(\\s*\\)"));
        functionSQLServer.put(SQLFunction.LIMIT_RESULT, new Pair<>(" TOP ", "(?i)top\\s*\\(?(\\s*[0-9]+\\s*)\\)?"));
        functionSQLServer.put(SQLFunction.CONVERT_DATE, new Pair<>(" CONVERT(VARCHAR(10), CAST(* AS DATE), 105)  ", "(?i)CONVERT\\s*\\(\\s*VARCHAR\\s*\\(\\s*\\d+\\s*\\)\\s*,\\s*CAST\\s*\\((\\s*\\w+\\s*)\\s+AS\\s+\\w+\\s*\\)\\s*,\\s*\\d+\\s*\\)\\s*"));
        functionSQLServer.put(SQLFunction.INTERVAL, new Pair<>(" BETWEEN DATEADD(day, 1, GETDATE()) AND DATEADD(month, 10, GETDATE()) ", "BETWEEN DATEADD\\(day, 1, GETDATE\\(\\)\\) AND DATEADD\\(month, 10, GETDATE\\(\\)\\)"));

        functionMySQL.put(SQLFunction.CURRENT_TIME, new Pair<>(" NOW() ", "(?i)now\\(\\s*\\)"));
        functionMySQL.put(SQLFunction.LIMIT_RESULT, new Pair<>(" LIMIT ", "(?i)limit\\s+(\\d+)\\s*;?"));
        functionMySQL.put(SQLFunction.CONVERT_DATE, new Pair<>(" DATE_FORMAT(CAST(* AS DATE), '%d-%m-%Y') ", "(?i)DATE_FORMAT\\s*\\(\\s*CAST\\s*\\((\\s*\\w+\\s*)\\s+AS\\s+\\w+\\s*\\)\\s*,\\s*\\'%d-%m-%Y'\\s*\\)\\s*"));
        functionMySQL.put(SQLFunction.INTERVAL, new Pair<>(" BETWEEN DATE_ADD(NOW(), INTERVAL 1 DAY) AND DATE_ADD(NOW(), INTERVAL 10 MONTH) ", "BETWEEN DATE_ADD\\(NOW\\(\\), INTERVAL 1 DAY\\) AND DATE_ADD\\(NOW\\(\\), INTERVAL 10 MONTH\\)"));
    }

    /**
     * Parse the local query related to the environment [local, stable, cyhourly, cynightly]
     *
     * @param aLocalQuery query to parse before calling jdbcTemplate.query[..]
     * @return
     */
    public static String parse(String aLocalQuery) {
        String myQuery;
        try {
            myQuery = aLocalQuery;
            //## remove square bracket (only for local use)
            myQuery = removeSquareBracket(myQuery);
            //## remove dbo syntax (only for local use)
            myQuery = removeDBOSynatax(myQuery);
            //## convert sql function; at the moment the support is limited just to sqlserver and mysql
            myQuery = convertFunctionForSQLGrammar(myQuery);

            return myQuery;
        } catch (Exception e) {
            System.out.println("Error while parsing local query! Return same value");
            return aLocalQuery;
        }
    }

    /**
     * Match a regex in a string source
     *
     * @param regex  to match
     * @param source string
     * @return result of campairison
     */
    private static boolean matchResult(String regex, String source) {
        Matcher m = Pattern.compile(regex).matcher(source);
        return m.find();
    }

    private static int getGroupMatch(String regex, String source) {
        Matcher m = Pattern.compile(regex).matcher(source);
        int count = 0;
        while (m.find())
            count++;

        return count;
    }

    /**
     * Return as value a number inside a string.
     * At the moment the full match is garanted just for the first occurance of a value
     *
     * @param regex  to match
     * @param source string
     * @param group  of the string you want match
     * @return the value
     */
    private static int getValue(String regex, String source, int group) {
        Matcher m = Pattern.compile(regex).matcher(source);
        return m.find() ? Integer.parseInt(m.group(group)) : -1;
    }

    /**
     * Return a particular word inside a regex.
     * At the moment it require the word you want to match and build the regex to recognize the word as a particular group
     *
     * @param regex  to match
     * @param source string
     * @param group  of the string you want match
     * @return
     */
    private static String getValueAsString(String regex, String source, int group) {
        Matcher m = Pattern.compile(regex).matcher(source);
        return m.find() ? m.group(group) : "";
    }

    /**
     * Replace a word inside a regex
     *
     * @param regex       to match
     * @param source      string
     * @param replacement word to raplace inside regex
     * @return
     */
    private static String replace(String regex, String source, String replacement) {
        return source.replaceAll(regex, replacement);
    }

    /**
     * Replace the first occurrence of a word inside a regex
     *
     * @param regex       to match
     * @param source      string
     * @param replacement word to raplace inside regex
     * @return
     */
    private static String replaceFirst(String regex, String source, String replacement) {
        return source.replaceFirst(regex, replacement);
    }

    /**
     * Remove square bracket to run under mysql (local env)
     *
     * @return query without square bracket
     */
    private static String removeSquareBracket(String aQuery) {
        return ENVIRONMENT.equals("local") ? aQuery.replace("[", "").replace("]", "") : aQuery;
    }

    /**
     * Remove 'dbo.' syntax to run under mysql (local env)
     *
     * @return query without 'dbo.' syntax
     */
    private static String removeDBOSynatax(String aQuery) {
        return ENVIRONMENT.equals("local") ? aQuery.replace("dbo.", "") : aQuery;
    }

    /**
     * Convert the grammar of the query based on sql running (mysql or sqlserver)
     *
     * @return query parsed for different environemnt
     */
    private static String convertFunctionForSQLGrammar(String aQuery) {

        Map<SQLFunction, Pair> functionAnalizer;
        Map<SQLFunction, Pair> oppositeFuncition;
        Pair item;
        String query = aQuery;

        if (ENVIRONMENT.equals("local")) {
            functionAnalizer = functionSQLServer;
            oppositeFuncition = functionMySQL;
        } else {
            functionAnalizer = functionMySQL;
            oppositeFuncition = functionSQLServer;
        }

        for (SQLFunction key : functionAnalizer.keySet()) {
            item = functionAnalizer.get(key);

            if (matchResult((String) item.getValue(), query)) {
                switch (key) {
                    case LIMIT_RESULT: //## replace the function LIMIT, TOP
                        int getValue = getValue((String) item.getValue(), query, 1);
                        if (ENVIRONMENT.equals("local")) {
                            String replacement = (String) oppositeFuncition.get(key).getKey() + getValue + ";";
                            query = replace((String) item.getValue(), query, " ");
                            query = query.contains(";") ? replace(";", query, replacement) : (query + " " + replacement);
                        } else {
                            String regexString = "(?i)select\\s*";
                            String replacement = "SELECT " + oppositeFuncition.get(key).getKey() + "(" + getValue + ") ";
                            query = replace(regexString, query, replacement);
                            query = replace((String) item.getValue(), query, ";");
                        }
                        break;
                    case CURRENT_TIME: //## replace the function NOW(), GETDATE()
                        query = replace((String) item.getValue(), query, (String) oppositeFuncition.get(key).getKey());
                        break;
                    case CONVERT_DATE: //## replace the function CONVERT, DATEFORMAT
                        int groupMatch = getGroupMatch((String) item.getValue(), query);
                        for (int i = 1; i <= groupMatch; i++) {
                            String columnValue = getValueAsString((String) item.getValue(), query, 1);
                            String replacement = ((String) oppositeFuncition.get(key).getKey()).replace("*", columnValue) + " ";
                            query = replaceFirst((String) item.getValue(), query, replacement);
                        }
                        break;
                    case INTERVAL: //## replace the function BETWEEN date AND date
                        query = replace((String) item.getValue(), query, (String) oppositeFuncition.get(key).getKey());
                        break;
                }
            }
        }

        return query;
    }
}
