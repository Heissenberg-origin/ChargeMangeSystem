package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.entity.ChargeItemsInfo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Mapper
public interface ChargeItemsInfoMapper extends BaseMapper<ChargeItemsInfo> {

    // ==================== 基础CRUD操作 ====================

    /**
     * 插入收费项目
     *
     * @param chargeItemsInfo 收费项目信息
     * @param creator         创建者
     */
    @Insert("INSERT INTO chargeitems_info (" +
            "chargeitem_type, chargeitem_name, chargeitem_code, chargeitem_ex_dep_id, " +
            "chargeitem_method, chargeitem_unit, chargeitem_balance, chargeitem_price, " +
            "chargeitem_state, chargeitem_creator, chargeitem_time, chargeitem_lattest_fixer" +
            ") VALUES (" +
            "#{chargeItemsInfo.chargeItemType.displayValue}, #{chargeItemsInfo.chargeItemName}, #{chargeItemsInfo.chargeItemCode}, #{chargeItemsInfo.chargeItemExDepId}, " +
            "#{chargeItemsInfo.chargeItemMethod}, #{chargeItemsInfo.chargeItemUnit}, #{chargeItemsInfo.chargeItemBalance}, #{chargeItemsInfo.chargeItemPrice}, " +
            "#{chargeItemsInfo.chargeItemState.displayValue}, #{creator}, NOW(), #{creator}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "chargeItemsInfo.chargeItemId")
    void insertInfo(ChargeItemsInfo chargeItemsInfo, String creator);

    /**
     * 查询所有收费项目
     *
     * @return 收费项目列表
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        """)
    @Results(id = "chargeItemResultMap", value = {
            @Result(property = "chargeItemId", column = "chargeitem_id"),
            @Result(property = "chargeItemType", column = "chargeitem_type"),
            @Result(property = "chargeItemName", column = "chargeitem_name"),
            @Result(property = "chargeItemCode", column = "chargeitem_code"),
            @Result(property = "chargeItemExDepId", column = "chargeitem_ex_dep_id"),
            @Result(property = "depname", column = "depName"),
            @Result(property = "chargeItemMethod", column = "chargeitem_method"),
            @Result(property = "chargeItemUnit", column = "chargeitem_unit"),
            @Result(property = "chargeItemBalance", column = "chargeitem_balance"),
            @Result(property = "chargeItemPrice", column = "chargeitem_price"),
            @Result(property = "chargeItemState", column = "chargeitem_state"),
            @Result(property = "chargeItemCreator", column = "chargeitem_creator"),
            @Result(property = "chargeItemTime", column = "chargeitem_time"),
            @Result(property = "chargeItemLatestFixer", column = "chargeitem_lattest_fixer")
    })
    List<ChargeItemsInfo> selectAll();

    /**
     * 根据ID查询收费项目
     *
     * @param id 收费项目ID
     * @return 收费项目信息
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        WHERE
            ci.chargeitem_id = #{id}
        """)
    @ResultMap("chargeItemResultMap")
    ChargeItemsInfo selectById(int id);

    /**
     * 更新收费项目信息
     *
     * @param id               收费项目ID
     * @param chargeItemsInfo  更新后的收费项目信息
     * @param fixer            修订者
     */
    @Update("UPDATE chargeitems_info SET " +
            "chargeitem_name = #{chargeItemsInfo.chargeItemName}, " +
            "chargeitem_type = #{chargeItemsInfo.chargeItemType.displayValue}, " +
            "chargeitem_code = #{chargeItemsInfo.chargeItemCode}, " +
            "chargeitem_ex_dep_id = #{chargeItemsInfo.chargeItemExDepId}, " +
            "chargeitem_method = #{chargeItemsInfo.chargeItemMethod}, " +
            "chargeitem_unit = #{chargeItemsInfo.chargeItemUnit}, " +
            "chargeitem_balance = #{chargeItemsInfo.chargeItemBalance}, " +
            "chargeitem_price = #{chargeItemsInfo.chargeItemPrice}, " +
            "chargeitem_state = #{chargeItemsInfo.chargeItemState.displayValue}, " +
            "chargeitem_lattest_fixer = #{fixer} " +
            "WHERE chargeitem_id = #{id}")
    void updateInfo(int id, ChargeItemsInfo chargeItemsInfo, String fixer);

    /**
     * 根据ID删除收费项目
     *
     * @param id 收费项目ID
     */
    @Delete("DELETE FROM chargeitems_info WHERE chargeitem_id = #{id}")
    void delete(int id);

    // ==================== 业务查询操作 ====================

    /**
     * 根据类型查询收费项目
     *
     * @param type 收费项目类型
     * @return 收费项目列表
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        WHERE ci.chargeitem_type = #{type.displayValue}
        """)
    @ResultMap("chargeItemResultMap")
    List<ChargeItemsInfo> selectByType(ChargeItemsInfo.ChargeItemType type);

    /**
     * 根据部门ID查询收费项目
     *
     * @param departmentId 部门ID
     * @return 收费项目列表
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        WHERE
            ci.chargeitem_ex_dep_id = #{departmentId}
        """)
    @ResultMap("chargeItemResultMap")
    List<ChargeItemsInfo> selectByDepartment(int departmentId);

    /**
     * 根据状态查询收费项目
     *
     * @param status 收费项目状态
     * @return 收费项目列表
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        WHERE
            ci.chargeitem_state = #{status.displayValue}
        """)
    @ResultMap("chargeItemResultMap")
    List<ChargeItemsInfo> selectByStatus(ChargeItemsInfo.ItemState status);

    /**
     * 根据关键字模糊查询收费项目
     *
     * @param keyword 查询关键字
     * @return 收费项目列表
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        WHERE
            ci.chargeitem_name LIKE CONCAT('%', #{keyword}, '%') OR chargeitem_code LIKE CONCAT('%', #{keyword}, '%')
        """)
    @ResultMap("chargeItemResultMap")
    List<ChargeItemsInfo> searchByKeyword(String keyword);

    /**
     * 查询低余额的收费项目
     *
     * @param threshold 余额阈值
     * @return 余额低于阈值的收费项目列表
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        WHERE
            ci.chargeitem_balance < #{threshold}
        """)
    @ResultMap("chargeItemResultMap")
    List<ChargeItemsInfo> selectWithLowBalance(int threshold);

    /**
     * 根据价格范围查询收费项目
     *
     * @param minPrice 最小价格
     * @param maxPrice 最大价格
     * @return 指定价格范围内的收费项目列表
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        WHERE
            ci.chargeitem_price BETWEEN #{minPrice} AND #{maxPrice}
        """)
    @ResultMap("chargeItemResultMap")
    List<ChargeItemsInfo> selectByPriceRange(@Param("minPrice") double minPrice,
                                             @Param("maxPrice") double maxPrice);

    // ==================== 业务操作 ====================

    /**
     * 更新收费项目状态
     *
     * @param id       收费项目ID
     * @param status   更新后的状态
     * @param modifier 修订者
     */
    @Update("UPDATE chargeitems_info SET " +
            "chargeitem_state = #{status.displayValue}, " +
            "chargeitem_lattest_fixer = #{modifier} " +
            "WHERE chargeitem_id = #{id}")
    void updateStatus(@Param("id") int id,
                      @Param("status") ChargeItemsInfo.ItemState status,
                      @Param("modifier") String modifier);

    /**
     * 修改收费项目的余额
     *
     * @param id       收费项目ID
     * @param change   变更的金额
     * @param modifier 修订者
     * @return 影响的行数
     */
    @Update("UPDATE chargeitems_info SET " +
            "chargeitem_balance = #{change}, " +
            "chargeitem_lattest_fixer = #{modifier} " +
            "WHERE chargeitem_id = #{id}")
    int updateBalance(@Param("id") int id,
                      @Param("change") int change,
                      @Param("modifier") String modifier);

    // ==================== 统计查询操作 ====================

    /**
     * 查询所有收费项目的类型
     *
     * @return 收费项目类型列表
     */
    @Select("SELECT DISTINCT chargeitem_type FROM chargeitems_info")
    List<String> selectAllTypes();

    /**
     * 根据创建者查询收费项目
     *
     * @param creator 创建者
     * @return 收费项目列表
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        WHERE
            chargeitem_creator LIKE CONCAT('%', #{creator}, '%')
        """)
    @ResultMap("chargeItemResultMap")
    List<ChargeItemsInfo> selectByCreator(String creator);

    /**
     * 根据最后修订者查询收费项目
     *
     * @param modifier 修订者
     * @return 收费项目列表
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        WHERE
            chargeitem_lattest_fixer LIKE CONCAT('%', #{modifier}, '%')
        """)
    @ResultMap("chargeItemResultMap")
    List<ChargeItemsInfo> selectByModifier(String modifier);

    /**
     * 查询在指定时间范围内创建的收费项目
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 收费项目列表
     */
    @Select("""
        SELECT
            ci.chargeitem_id,
            ci.chargeitem_type,
            ci.chargeitem_name,
            ci.chargeitem_code,
            ci.chargeitem_ex_dep_id,
            ci.chargeitem_method,
            ci.chargeitem_unit,
            ci.chargeitem_balance,
            ci.chargeitem_price,
            ci.chargeitem_state,
            ci.chargeitem_creator,
            ci.chargeitem_time,
            ci.chargeitem_lattest_fixer,
            di.department_name as depName
        FROM
            chargeitems_info ci
        JOIN
            department_info di ON ci.chargeitem_ex_dep_id = di.department_id
        WHERE
            chargeitem_time BETWEEN #{startDate} AND #{endDate}
        """)
    @ResultMap("chargeItemResultMap")
    List<ChargeItemsInfo> selectCreatedBetween(@Param("startDate") Timestamp startDate,
                                               @Param("endDate") Timestamp endDate);

    /**
     * 查询收费项目的统计信息
     *
     * @return 统计信息
     */
    @Select("SELECT " +
            "COUNT(*) AS totalCount, " +
            "SUM(chargeitem_balance) AS totalBalance, " +
            "SUM(chargeitem_price * chargeitem_balance) AS totalValue, " +
            "AVG(chargeitem_price) AS averagePrice, " +
            "MIN(chargeitem_price) AS minPrice, " +
            "MAX(chargeitem_price) AS maxPrice " +
            "FROM chargeitems_info")
    Map<String, Object> selectStatistics();
}