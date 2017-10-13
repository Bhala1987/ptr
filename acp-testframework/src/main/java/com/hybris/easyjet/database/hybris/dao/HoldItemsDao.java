package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by giuseppedimartino on 15/03/17.
 */
@Repository
public class HoldItemsDao {

    public static final String PRICE = "price";
    public static final String QTY_FROM = "qtyFrom";
    public static final String PRICE1 = "Price";
    public static final String QUANTITY = "Quantity";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public HoldItemsDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map<String, List<HashMap<String, Double>>> returnActiveProducts(String channel, String currency) {

        return returnActiveProducts(channel, currency, null, null, null);
    }

    public Map<String, List<HashMap<String, Double>>> returnActiveProductsByFlightKey(String channel, String currency, String flightKey) {

        HashMap<String, List<HashMap<String, Double>>> productsForFlightKey = returnActiveProducts(channel, currency, flightKey, null, null);
        HashMap<String, List<HashMap<String, Double>>> productsForSector = returnActiveProducts(channel, currency, null, flightKey.substring(9, 16), null);
        HashMap<String, List<HashMap<String, Double>>> productsList = returnActiveProducts(channel, currency, null, null, null);

        for (HashMap.Entry<String, List<HashMap<String, Double>>> entry : productsList.entrySet()) {
            String key = entry.getKey();
            if (productsForFlightKey.containsKey(key)) {
                productsList.put(key, productsForFlightKey.get(key));
            } else if (productsForSector.containsKey(key)) {
                productsList.put(key, productsForSector.get(key));
            }
        }

        return productsList;

    }

    public Map<String, List<HashMap<String, Double>>> returnActiveProductsBySector(String channel, String currency, String sector) {

        HashMap<String, List<HashMap<String, Double>>> productsForSector = returnActiveProducts(channel, currency, null, sector, null);
        HashMap<String, List<HashMap<String, Double>>> productsList = returnActiveProducts(channel, currency, null, null, null);

        for (HashMap.Entry<String, List<HashMap<String, Double>>> entry : productsForSector.entrySet()) {
            String key = entry.getKey();
            List<HashMap<String, Double>> value = entry.getValue();
            productsList.put(key, value);
        }

        return productsList;
    }

    public Map<String, List<HashMap<String, Double>>> returnActiveProductsByBundle(String channel, String currency, String bundle) {

        HashMap<String, List<HashMap<String, Double>>> productsForBundle = returnActiveProductsForBundle(currency, bundle);
        HashMap<String, List<HashMap<String, Double>>> productsList = returnActiveProducts(channel, currency, null, null, null);

        for (HashMap.Entry<String, List<HashMap<String, Double>>> entry : productsForBundle.entrySet()) {
            String key = entry.getKey();
            List<HashMap<String, Double>> value = entry.getValue();
            productsList.put(key, value);
        }

        return productsList;
    }

    public Map<String, List<HashMap<String, Double>>> returnActiveProductsByFlightKeyWithBundle(String channel, String currency, String flightKey, String bundle) {

        HashMap<String, List<HashMap<String, Double>>> productsForBundle = returnActiveProductsForBundle(currency, bundle);
        HashMap<String, List<HashMap<String, Double>>> productsForFlightKey = returnActiveProducts(channel, currency, flightKey, null, null);
        HashMap<String, List<HashMap<String, Double>>> productsForSector = returnActiveProducts(channel, currency, null, flightKey.substring(9, 16), null);
        HashMap<String, List<HashMap<String, Double>>> productsList = returnActiveProducts(channel, currency, null, null, null);

        for (HashMap.Entry<String, List<HashMap<String, Double>>> entry : productsList.entrySet()) {
            String key = entry.getKey();
            if (productsForFlightKey.containsKey(key)) {
                productsList.put(key, productsForFlightKey.get(key));
            } else if (productsForSector.containsKey(key)) {
                productsList.put(key, productsForSector.get(key));
            }
        }

        for (HashMap.Entry<String, List<HashMap<String, Double>>> entry : productsForBundle.entrySet()) {
            String key = entry.getKey();
            List<HashMap<String, Double>> value = entry.getValue();
            productsList.put(key, value);
        }

        return productsList;

    }

    public Map<String, List<HashMap<String, Double>>> returnActiveProductsBySectorWithBundle(String channel, String currency, String sector, String bundle) {

        HashMap<String, List<HashMap<String, Double>>> productsForBundle = returnActiveProductsForBundle(currency, bundle);
        HashMap<String, List<HashMap<String, Double>>> productsForSector = returnActiveProducts(channel, currency, null, sector, null);
        HashMap<String, List<HashMap<String, Double>>> productsList = returnActiveProducts(channel, currency, null, null, null);

        for (HashMap.Entry<String, List<HashMap<String, Double>>> entry : productsForSector.entrySet()) {
            String key = entry.getKey();
            List<HashMap<String, Double>> value = entry.getValue();
            productsList.put(key, value);
        }

        for (HashMap.Entry<String, List<HashMap<String, Double>>> entry : productsForBundle.entrySet()) {
            String key = entry.getKey();
            List<HashMap<String, Double>> value = entry.getValue();
            productsList.put(key, value);
        }

        return productsList;
    }

    private HashMap<String, List<HashMap<String, Double>>> returnActiveProducts(String channel, String currency, String flightKey, String sector, String userGroup) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency", currency)
                .addValue("flightKey", flightKey)
                .addValue("sector", sector)
                .addValue("channel", channel)
                .addValue("userGroup", userGroup);

        String query =
                "SELECT\n" +
                        "	p.p_code AS Name\n" +
                        "	,pr.p_price AS Price\n" +
                        "	,pr.p_minqtd AS Quantity\n" +
                        "FROM pricerows AS pr\n" +
                        "	LEFT JOIN enumerationvalues AS evc ON evc.pk = pr.p_salesapplication\n" +
                        "	JOIN currencies AS c ON c.pk = pr.p_currency\n" +
                        "	LEFT JOIN usergroups AS ug ON ug.pk = pr.p_ug\n" +
                        "	JOIN products AS p ON p.pk = pr.p_product\n" +
                        "	JOIN enumerationvalues AS eva ON eva.pk = p.p_approvalstatus\n" +
                        "	JOIN composedtypes AS ct ON ct.pk = p.TypePkString\n" +
                        "WHERE p.p_catalogversion IN (\n" +
                        "	SELECT pk\n" +
                        "	FROM catalogversions\n" +
                        "	WHERE p_version = 'Online'\n" +
                        "	)\n" +
                        "	AND ct.InternalCode IN (\n" +
                        "		'SmallSportsProduct'\n" +
                        "		,'LargeSportsProduct'\n" +
                        "		,'HoldBagProduct'\n" +
                        "		,'ExcessWeightProduct'\n" +
                        "	)\n" +
                        "	AND p.p_onlinedate < GETDATE()\n" +
                        "	AND p.p_offlinedate > GETDATE()\n" +
                        "	AND eva.Code = 'approved'\n" +
                        "	AND c.p_isocode = :currency\n" +
                        "	AND ( evc.Code in (:channel, 'All') or pr.p_salesapplication is null)";

        if (flightKey != null) {
            query += "\nAND pr.p_transportofferingcode = :flightKey";
        } else {
            query += "\nAND pr.p_transportofferingcode IS NULL";
        }

        if (sector != null) {
            query += "\nAND pr.p_travelsectorcode = :sector";
        } else {
            query += "\nAND pr.p_travelsectorcode IS NULL";
        }

        if (userGroup != null) {
            query += "\nAND ug.p_uid = :userGroup";
        } else {
            query += "\nAND ug.p_uid IS NULL";
        }

        return this.jdbcTemplate.query(QueryParser.parse(query), params, rs -> {
                    HashMap<String, List<HashMap<String, Double>>> productList = new HashMap<>();

                    while (rs.next()) {
                        String name = rs.getString("Name");
                        if (!productList.containsKey(name)) {
                            productList.put(name, new ArrayList<>());
                        }
                        HashMap<String, Double> result = new HashMap<>();
                        result.put(PRICE, rs.getDouble(PRICE1));
                        result.put(QTY_FROM, rs.getDouble(QUANTITY));
                        productList.get(name).add(result);
                    }

                    return productList;
                }
        );

    }

    private HashMap<String, List<HashMap<String, Double>>> returnActiveProductsForBundle(String currency, String bundle) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency", currency)
                .addValue("bundle", bundle);

        String query =
                "SELECT\n" +
                        "	p.p_code AS Name\n" +
                        "	,abr.p_price AS Price\n" +
                        "FROM abstractbundlerules AS abr\n" +
                        "	JOIN currencies AS c ON c.pk = abr.p_currency\n" +
                        "	JOIN bundletemplates AS bt ON bt.pk = abr.p_bundletemplate\n" +
                        "	JOIN bundlerule2targprodrel AS r2p ON r2p.SourcePK = abr.pk\n" +
                        "	JOIN products AS p ON p.pk = r2p.TargetPK\n" +
                        "	JOIN composedtypes AS ct ON ct.pk = p.TypePkString\n" +
                        "	JOIN enumerationvalues AS eva ON eva.pk = p.p_approvalstatus\n" +
                        "WHERE p.p_catalogversion IN (\n" +
                        "	SELECT pk\n" +
                        "	FROM catalogversions\n" +
                        "	WHERE p_version = 'Online'\n" +
                        ")\n" +
                        "	AND ct.InternalCode IN (\n" +
                        "		'SmallSportsProduct'\n" +
                        "		,'LargeSportsProduct'\n" +
                        "		,'HoldBagProduct'\n" +
                        "		,'ExcessWeightProduct'\n" +
                        "	)\n" +
                        "	AND p.p_onlinedate < GETDATE()\n" +
                        "	AND p.p_offlinedate > GETDATE()\n" +
                        "	AND eva.Code = 'approved'\n" +
                        "	AND c.p_isocode = :currency\n" +
                        "	AND bt.p_id = :bundle";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, rs -> {
                    HashMap<String, List<HashMap<String, Double>>> productList = new HashMap<>();

                    while (rs.next()) {
                        String name = rs.getString("Name");
                        if (!productList.containsKey(name)) {
                            productList.put(name, new ArrayList<>());
                        }
                        HashMap<String, Double> result = new HashMap<>();
                            result.put(PRICE, rs.getDouble("Price"));
                            result.put(QTY_FROM, (double) 1);
                        productList.get(name).add(result);
                    }

                    return productList;
                }
        );
    }

    /**
     * Utility method for sport equipment hold item
     */

    public List<String> getItemsSportEquipment(String internalCode) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("InternalCode", internalCode);
        String query = "SELECT p_code\n" +
                "FROM products JOIN composedtypes ON products.TypePkString = composedtypes.PK\n" +
                "WHERE composedtypes.InternalCode = :InternalCode;";

        return this.jdbcTemplate.query(QueryParser.parse(query), parameterSource, (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<String> getItemsHoldBag(String internalCode) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("InternalCode", internalCode);
        String query = "SELECT p_code\n" +
                "FROM ej_core.products JOIN composedtypes ON products.TypePkString = composedtypes.PK\n" +
                "WHERE composedtypes.InternalCode = :InternalCode;";

        return this.jdbcTemplate.query(QueryParser.parse(query), parameterSource, (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<String> getAllItemsSportEquipment() {
        String query = "SELECT p_code\n" +
                "FROM products JOIN composedtypes ON products.TypePkString = composedtypes.PK\n" +
                "WHERE (composedtypes.InternalCode = 'SportsProduct'\n" +
                "OR composedtypes.InternalCode = 'SmallSportsProduct'\n" +
                "OR composedtypes.InternalCode = 'LargeSportsProduct');";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<String> getAllItemsHoldBag() {
        String query = "SELECT p_code\n" +
                "FROM ej_core.products JOIN composedtypes ON products.TypePkString = composedtypes.PK\n" +
                "WHERE (composedtypes.InternalCode = 'HoldBagProduct')";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<String> getStockLevelForFlight(String flightKey, String productCode) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("PK", flightKey)
                .addValue("p_code", productCode);

        String query = "SELECT p_available \n" +
                "FROM stocklevels JOIN warehouses ON stocklevels.p_warehouse = warehouses.PK\n" +
                "JOIN products ON stocklevels.p_productcode = products.p_code\n" +
                "WHERE warehouses.p_code = :PK\n" +
                "AND products.p_code = :p_code;";

        return this.jdbcTemplate.query(QueryParser.parse(query), parameterSource, (rs, rowNum) -> rs.getString("p_available"));
    }

    public List<String> getReservedStockLevelForFlight(String flightKey, String productCode) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("PK", flightKey)
                .addValue("p_code", productCode);

        String query = "SELECT p_reserved \n" +
                "FROM stocklevels JOIN warehouses ON stocklevels.p_warehouse = warehouses.PK\n" +
                "JOIN products ON stocklevels.p_productcode = products.p_code\n" +
                "WHERE warehouses.p_code = :PK\n" +
                "AND products.p_code = :p_code;";

        return this.jdbcTemplate.query(QueryParser.parse(query), parameterSource, (rs, rowNum) -> rs.getString("p_reserved"));
    }

    public List<String> getValidFlightKeyForStockLevel() {

        String query = "SELECT TOP(300) warehouses.p_code\n" +
                "FROM warehouses JOIN stocklevels ON warehouses.PK = stocklevels.p_warehouse\n" +
                "AND stocklevels.p_productcode IN (\n" +
                "\tSELECT p_code\n" +
                "\tFROM products JOIN composedtypes ON products.TypePkString = composedtypes.PK\n" +
                "\tWHERE (composedtypes.InternalCode = 'SportsProduct'\n" +
                "\tOR composedtypes.InternalCode = 'SmallSportsProduct'\n" +
                "\tOR composedtypes.InternalCode = 'LargeSportsProduct')\n" +
                ")\n" +
                "GROUP BY warehouses.p_code;";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<String> getValidFlightKeyForStockLevelForHoldBag() {

        String query = "SELECT warehouses.p_code\n" +
                "FROM ej_core.warehouses JOIN stocklevels ON warehouses.PK = stocklevels.p_warehouse\n" +
                "AND stocklevels.p_productcode IN (\n" +
                "\tSELECT p_code\n" +
                "\tFROM ej_core.products JOIN composedtypes ON products.TypePkString = composedtypes.PK\n" +
                "\tWHERE (composedtypes.InternalCode = 'HoldBagProduct'\n" +
                ")\n" +
                "GROUP BY warehouses.p_code;";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<String> getThresholdForPassengerMix(String channel, String passengerType, String productCategory, boolean isInfantOnLap) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("InternalCode", productCategory)
                .addValue("p_code", passengerType)
                .addValue("Code", channel);

        String query = "SELECT p_maxpermitted\n" +
                "FROM passengertypecapping JOIN composedtypes ON passengertypecapping.p_producttype = composedtypes.PK\n" +
                "JOIN passengertype ON passengertypecapping.p_passengertype = passengertype.PK\n" +
                "JOIN enumerationvalues ON passengertypecapping.p_channel = enumerationvalues.PK\n" +
                "WHERE composedtypes.InternalCode = :InternalCode\n" +
                "AND passengertype.p_code = :p_code\n" +
                "AND enumerationvalues.Code =:Code";
        if (isInfantOnLap) {
            query = query + " AND p_hasinfantonlap=1;";
        } else {
            query = query + " AND p_hasinfantonlap=0;";
        }

        return this.jdbcTemplate.query(QueryParser.parse(query), parameterSource, (rs, rowNum) -> rs.getString("p_maxpermitted"));
    }

    public Double getProductPrice(String channel, String currency, String flightKey, String sector, String fareType, String code, int quantity) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency", currency)
                .addValue("flightKey", flightKey)
                .addValue("sector", sector)
                .addValue("channel", channel)
                .addValue("fareType", fareType)
                .addValue("code", code);

        String baseQuery =
                "SELECT\n" +
                        "p.p_code AS Name\n" +
                        ",pr.p_price AS Price\n" +
                        ",pr.p_minqtd AS Quantity\n" +
                        "FROM pricerows AS pr\n" +
                        "	JOIN enumerationvalues AS evc ON evc.pk = pr.p_salesapplication\n" +
                        "	JOIN currencies AS c ON c.pk = pr.p_currency\n" +
                        "	LEFT JOIN usergroups AS ug ON ug.pk = pr.p_ug\n" +
                        "	JOIN products AS p ON p.pk = pr.p_product\n" +
                        "	JOIN enumerationvalues AS eva ON eva.pk = p.p_approvalstatus\n" +
                        "	JOIN composedtypes AS ct ON ct.pk = p.TypePkString\n" +
                        "WHERE p.p_catalogversion IN (\n" +
                        "	SELECT pk\n" +
                        "	FROM catalogversions\n" +
                        "	WHERE p_version = 'Online'\n" +
                        "	)\n" +
                        "	AND ct.InternalCode IN (\n" +
                        "		'SmallSportsProduct'\n" +
                        "		,'LargeSportsProduct'\n" +
                        "		,'HoldBagProduct'\n" +
                        "		,'ExcessWeightProduct'\n" +
                        "	)\n" +
                        "   AND p.p_code = :code\n" +
                        "	AND p.p_onlinedate < GETDATE()\n" +
                        "	AND p.p_offlinedate > GETDATE()\n" +
                        "	AND eva.Code = 'approved'\n" +
                        "	AND c.p_isocode = :currency\n" +
                        "	AND (evc.Code = :channel OR evc.Code = 'All')\n";

        String query = baseQuery +
                "AND pr.p_transportofferingcode = :flightKey;";

        List<HashMap<String, Double>> productList = new ArrayList<>();
        this.jdbcTemplate.query(QueryParser.parse(query), params, rs -> {
                    while (rs.next()) {
                        HashMap<String, Double> result = new HashMap<>();
                            result.put(PRICE, rs.getDouble(PRICE1));
                            result.put(QTY_FROM, rs.getDouble(QUANTITY));
                        productList.add(result);
                    }

                    return productList;
                }
        );

        if (!productList.isEmpty()) {
            return productList.stream()
                    .filter(product -> product.get(QTY_FROM) <= quantity)
                    .max(Comparator.comparingDouble(p -> p.get(PRICE)))
                    .get().get(PRICE);
        }

        query = baseQuery +
                "AND pr.p_travelsectorcode = :sector;";

        this.jdbcTemplate.query(QueryParser.parse(query), params, rs -> {
                    while (rs.next()) {
                        HashMap<String, Double> result = new HashMap<>();
                            result.put(PRICE, rs.getDouble(PRICE1));
                            result.put(QTY_FROM, rs.getDouble(QUANTITY));
                        productList.add(result);
                    }

                    return productList;
                }
        );

        if (!productList.isEmpty()) {
            return productList.stream()
                    .filter(product -> product.get(QTY_FROM) <= quantity)
                    .max(Comparator.comparingDouble(p -> p.get(PRICE)))
                    .get().get(PRICE);
        }

        query = baseQuery +
                "AND pr.p_travelsectorcode = :fareType;";

        this.jdbcTemplate.query(QueryParser.parse(query), params, rs -> {
                    while (rs.next()) {
                        HashMap<String, Double> result = new HashMap<>();
                            result.put(PRICE, rs.getDouble(PRICE1));
                            result.put(QTY_FROM, rs.getDouble(QUANTITY));
                        productList.add(result);
                    }

                    return productList;
                }
        );

        if (!productList.isEmpty()) {
            return productList.stream()
                    .filter(product -> product.get(QTY_FROM) <= quantity)
                    .max(Comparator.comparingDouble(p -> p.get(PRICE)))
                    .get().get(PRICE);
        }

        this.jdbcTemplate.query(QueryParser.parse(baseQuery), params, rs -> {
                    while (rs.next()) {
                        HashMap<String, Double> result = new HashMap<>();
                            result.put(PRICE, rs.getDouble(PRICE1));
                            result.put(QTY_FROM, rs.getDouble(QUANTITY));
                        productList.add(result);
                    }

                    return productList;
                }
        );

        return productList.stream()
                .filter(product -> product.get(QTY_FROM) <= quantity)
                .max(Comparator.comparingDouble(p -> p.get(PRICE)))
                .get().get(PRICE);

    }

    public void updateTheStock(String flightKey, String productCode, String reservedQuantityToSet) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("flightKey", flightKey)
                .addValue("productCode", productCode)
                .addValue("reservedQuantityToSet", reservedQuantityToSet);

        String query = "UPDATE stocklevels \n" +
                "SET p_reserved = :reservedQuantityToSet \n" +
                "WHERE p_productcode = :productCode \n" +
                "AND p_warehouse = \n" +
                "(SELECT pk FROM warehouses WHERE p_code = :flightKey)";

        this.jdbcTemplate.update(QueryParser.parse(query), params);
}
}
