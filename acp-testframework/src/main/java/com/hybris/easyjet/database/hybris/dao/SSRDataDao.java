package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.SSRDataModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by giuseppecioce on 09/02/2017.
 */
@Repository
public class SSRDataDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public SSRDataDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<SSRDataModel> getSSRDataForEmptySector(boolean active, String pkChannel) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_active", active)
                .addValue("p_channels", pkChannel);

        String query =
                "SELECT\n" +
                        "[p_code]\n" +
                        ",[p_active]\n" +
                        "FROM [dbo].[specialservicerequest] JOIN ssr2channelrel ON specialservicerequest.PK = ssr2channelrel.SourcePK\n" +
                        "LEFT JOIN ssr2sectorrel ON specialservicerequest.PK = ssr2sectorrel.SourcePK " +
                        "WHERE specialservicerequest.p_active = :p_active\n" +
                        "AND ssr2channelrel.TargetPK = :p_channels;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new SSRDataModel(
                rs.getString("p_code"),
                rs.getBoolean("p_active")));
    }

    public List<String> getSSRDataActive(boolean active, int number) {

        SqlParameterSource params = new MapSqlParameterSource("p_active", active);

        String query =
                "SELECT TOP (" + number + ") [p_code]\n" +
                        "FROM [dbo].[specialservicerequest]\n" +
                        "WHERE specialservicerequest.p_active = :p_active\n" +
                        "GROUP BY specialservicerequest.p_code;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<String> getSSRsForTermsAndConditionsMandatoryToBeAccepted(boolean active, int number) {

        SqlParameterSource params = new MapSqlParameterSource("p_active", active).addValue("p_tec", true);

        String query =
                "SELECT TOP (" + number + ") [p_code]\n" +
                        "FROM [dbo].[specialservicerequest]\n" +
                        "WHERE specialservicerequest.p_active = :p_active\n" +
                        "And specialservicerequest.p_accepttermsandconditions = :p_tec \n" +
                        "GROUP BY specialservicerequest.p_code;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<String> getSSRsForTermsAndConditionsNotMandatoryToBeAccepted(boolean active, int number) {

        SqlParameterSource params = new MapSqlParameterSource("p_active", active);

        String query =
                "SELECT TOP (" + number + ") [p_code]\n" +
                        "FROM [dbo].[specialservicerequest]\n" +
                        "WHERE specialservicerequest.p_active = :p_active\n" +
                        "And specialservicerequest.p_accepttermsandconditions is NULL\n" +
                        "GROUP BY specialservicerequest.p_code;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<SSRDataModel> getSSRDataForValidSector(boolean active, String pkChannel, String pkSector) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_active", active)
                .addValue("p_channels", pkChannel)
                .addValue("p_sectors", pkSector);

        String query =
                "SELECT p_code\n" +
                        ",p_active\n" +
                        "FROM specialservicerequest\n" +
                        "LEFT JOIN ssr2channelrel ON specialservicerequest.PK = ssr2channelrel.SourcePK\n" +
                        "LEFT JOIN ssr2sectorrel ON specialservicerequest.PK = ssr2sectorrel.SourcePK\n" +
                        "WHERE ssr2channelrel.TargetPK = :p_channels\n" +
                        "AND ssr2sectorrel.TargetPK = :p_sectors\n" +
                        "UNION ALL\n" +
                        "SELECT p_code\n" +
                        ",p_active\n" +
                        "FROM specialservicerequest\n" +
                        "LEFT JOIN ssr2channelrel ON specialservicerequest.PK = ssr2channelrel.SourcePK\n" +
                        "WHERE ssr2channelrel.TargetPK = :p_channels\n" +
                        "AND specialservicerequest.PK NOT IN (\n" +
                        "SELECT ssr2sectorrel.SourcePK\n" +
                        "FROM ssr2sectorrel\n" +
                        ")\n" +
                        "AND specialservicerequest.p_active = :p_active;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new SSRDataModel(
                rs.getString("p_code"),
                rs.getBoolean("p_active")));
    }


    public List<SSRDataModel> getValidSSRForChannelAndSector(boolean active, String aChannel, String aSector) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_active", active)
                .addValue("p_channel", aChannel)
                .addValue("p_sector", aSector);

        String ssrForSectorList =
                "select specialservicerequest.p_code,p_active from specialservicerequest\n" +
                        "inner join ssr2channelrel ssrchan on ssrchan.SourcePK=specialservicerequest.PK\n" +
                        "inner join enumerationvalues on enumerationvalues.PK=ssrchan.TargetPK\n" +
                        "inner join ssr2sectorrel ssrsec on ssrsec.SourcePK=specialservicerequest.PK\n" +
                        "inner join travelsector travsec on travsec.pk = ssrsec.TargetPK\n" +
                        "where Code = :p_channel\n" +
                        "and travsec.p_code = :p_sector\n" +
                        "and specialservicerequest.p_active=:p_active;\n";

        return this.jdbcTemplate.query(QueryParser.parse(ssrForSectorList), namedParameters, (rs, rowNum) -> new SSRDataModel(
                rs.getString("p_code"),
                rs.getBoolean("p_active")));

    }

    public List<SSRDataModel> getInvalidSSRForChannelAndSector(boolean active, String aChannel, String aSector) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_active", active)
                .addValue("p_channel", aChannel)
                .addValue("p_sector", aSector);

        String ssrForSectorList =
                "select specialservicerequest.p_code,p_active from specialservicerequest\n" +
                        "inner join ssr2channelrel ssrchan on ssrchan.SourcePK=specialservicerequest.PK\n" +
                        "inner join enumerationvalues on enumerationvalues.PK=ssrchan.TargetPK\n" +
                        "inner join ssr2sectorrel ssrsec on ssrsec.SourcePK=specialservicerequest.PK\n" +
                        "inner join travelsector travsec on travsec.pk = ssrsec.TargetPK\n" +
                        "where Code = :p_channel\n" +
                        "and travsec.p_code = :p_sector\n" +
                        "and specialservicerequest.p_active=:p_active;\n";

        String ssrNotChosenForSector =
                "select p_code,p_active from specialservicerequest where p_code not in\n" +
                        "(select specialservicerequest.p_code from specialservicerequest\n" +
                        "inner join ssr2channelrel ssrchan on ssrchan.SourcePK=specialservicerequest.PK\n" +
                        "inner join enumerationvalues on enumerationvalues.PK=ssrchan.TargetPK\n" +
                        "inner join ssr2sectorrel ssrsec on ssrsec.SourcePK=specialservicerequest.PK\n" +
                        "inner join travelsector travsec on travsec.pk = ssrsec.TargetPK\n" +
                        "where Code = :p_channel\n" +
                        "and travsec.p_code = :p_sector\n" +
                        "and specialservicerequest.p_active=:p_active);\n";

        String ssrUsedElsewhereForOtherSectors =
                "select p_code,p_active from specialservicerequest where p_code in\n" +
                        "(select specialservicerequest.p_code from specialservicerequest\n" +
                        "inner join ssr2channelrel ssrchan on ssrchan.SourcePK=specialservicerequest.PK\n" +
                        "inner join enumerationvalues on enumerationvalues.PK=ssrchan.TargetPK\n" +
                        "inner join ssr2sectorrel ssrsec on ssrsec.SourcePK=specialservicerequest.PK\n" +
                        "inner join travelsector travsec on travsec.pk = ssrsec.TargetPK\n" +
                        "where Code = :p_channel\n" +
                        "and travsec.p_code <> :p_sector\n" +
                        "and specialservicerequest.p_active=:p_active);\n";


        List<SSRDataModel> sectorListResult = this.jdbcTemplate.query(QueryParser.parse(ssrForSectorList), namedParameters, (rs, rowNum) -> new SSRDataModel(
                rs.getString("p_code"),
                rs.getBoolean("p_active")));

        if (sectorListResult.size() == 0) {
            return this.jdbcTemplate.query(QueryParser.parse(ssrUsedElsewhereForOtherSectors), namedParameters, (rs, rowNum) -> new SSRDataModel(
                    rs.getString("p_code"),
                    rs.getBoolean("p_active")));
        } else {
            return this.jdbcTemplate.query(QueryParser.parse(ssrNotChosenForSector), namedParameters, (rs, rowNum) -> new SSRDataModel(
                    rs.getString("p_code"),
                    rs.getBoolean("p_active")));
        }

    }

    public List<SSRDataModel> getInvalidSSRForChannelAndSector(String aChannel, String aSector) {
        return this.getInvalidSSRForChannelAndSector(true, aChannel, aSector);
    }
}
