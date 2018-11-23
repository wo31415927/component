package jdk7.io.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/** @author cctv 2018/2/27 */
public class Calculator {
  private static final ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");

  public static Object cal(String expression) throws ScriptException {

    return jse.eval(expression);
  }
}
