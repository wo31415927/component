package com.study;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SqlParserTest {
  String sql =
      //      "select t1.id,t1.name,t2.id,t2.time,t2.status,t2.amount from user t1 inner join order
      // t2 on t1.id = t2.user_id";
      "select t1.id as id1,concat(t1.name,t1.sex) key from user t1";

  @Test
  public void test_select_items() throws JSQLParserException {
    CCJSqlParserManager parserManager = new CCJSqlParserManager();
    Select select = (Select) parserManager.parse(new StringReader(sql));
    PlainSelect plain = (PlainSelect) select.getSelectBody();
    List<SelectItem> selectitems = plain.getSelectItems();
    List<String> str_items = new ArrayList<>();
    if (selectitems != null) {
      for (int i = 0; i < selectitems.size(); i++) {
        str_items.add(selectitems.get(i).toString());
      }
    }
    System.out.println(str_items);
  }

  @Test
  public void test_select_table() throws JSQLParserException {
    Statement statement = CCJSqlParserUtil.parse(sql);
    Select selectStatement = (Select) statement;
    TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
    List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
    System.out.println(tableList);
  }
}
