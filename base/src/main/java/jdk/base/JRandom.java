package jdk.base;

import java.security.SecureRandom;

/** zeyu 2017/10/30 */
public class JRandom {
  public static void main(String args[]) throws Exception {
    System.out.println("Ok: " + SecureRandom.getInstance("SHA1PRNG").nextLong());
  }
}
