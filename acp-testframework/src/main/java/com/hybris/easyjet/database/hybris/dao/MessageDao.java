package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.TenantBeanFactoryPostProcessor;
import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class MessageDao {


    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public MessageDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    public static MessageDao getMessageDaoFromSpring() {
        return (MessageDao) TenantBeanFactoryPostProcessor.getFactory().getBean("messageDao");
    }

    /**
     * @return the code of the last message
     */
    public List<String> getMessageOfTheday() {

        SqlParameterSource params = new MapSqlParameterSource();

        String query =
                "SELECT motd.p_code AS code\n" +
                        ",motd.p_messagestartdate AS startDate\n"+
                        "FROM messageoftheday AS motd\n" +
                        "WHERE motd.p_messagestartdate <= now()\n" +
                        "ORDER BY p_messagestartdate DESC;";


        List<HashMap<String, String>> allMessage = this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new HashMap<String, String>() {{
            put("code", rs.getString("code"));
            put("startDate", rs.getString("startDate").split(" ")[0]);
        }});

        List <String> codeMessage= new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String data =dateFormat.format(new Date());

        allMessage.forEach(p -> {
            if (p.get("startDate").split(" ")[0].equalsIgnoreCase(data)){
                codeMessage.add(p.get("code"));
            }
        });

        return codeMessage;

    }
}
