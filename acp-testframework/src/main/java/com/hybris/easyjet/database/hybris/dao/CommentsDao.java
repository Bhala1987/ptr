package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.CommentModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;


@Repository
public class CommentsDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public CommentsDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public CommentModel getCommentWithId(String aCommentCode) {

        SqlParameterSource params = new MapSqlParameterSource("comment_code", aCommentCode);
        String query =
                "SELECT\n" +
                        "en.code AS channel\n" +
                        ", c.createdTS AS creation\n" +
                        ", c.modifiedTS AS modification\n" +
                        ", ct.p_code AS type\n" +
                        ", e.p_uid AS employee\n" +
                        "FROM comments AS c\n" +
                        "   INNER JOIN ejemployee AS e ON c.p_author = e.PK\n" +
                        "   INNER JOIN commenttypes AS ct ON c.p_commenttype = ct.PK\n" +
                        "   INNER JOIN enumerationvalues AS en ON c.p_channel = en.PK\n" +
                        "WHERE c.p_code  = :comment_code;";


        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (rs, rowNum) -> CommentModel.builder()
                .channel(rs.getString("channel"))
                .commenttype(rs.getString("type"))
                .employee_uid(rs.getString("employee"))
                .createdTS(rs.getString("creation"))
                .modifiedTS(rs.getString("modification"))
                .build());
    }

}