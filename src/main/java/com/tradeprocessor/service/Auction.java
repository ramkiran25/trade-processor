package com.tradeprocessor.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Auction {
  static class Order {
    int price;
    int amount;

    Order(int price, int amount) {
      this.price = price;
      this.amount = amount;
    }


    public int getNumberOfBacklogOrders(int[][] orders) {
      PriorityQueue<Order> sell = new PriorityQueue<>((a, b) -> Integer.compare(a.price, b.price));
      PriorityQueue<Order> buy = new PriorityQueue<>((a, b) -> Integer.compare(b.price, a.price));
      for (int[] order : orders) {

        int price = order[0];
        int amount = order[1];
        int type = order[2];
        // so its a buy
        if (type == 0) {

          while (!sell.isEmpty() && amount > 0 && sell.peek().price <= price) {
            Order sellOrder = sell.poll();
            int matched = Math.min(amount, sellOrder.amount);
            amount -= matched;
            sellOrder.amount -= matched;
            if (sellOrder.amount > 0) {
              sell.offer(sellOrder);
            }
          }
        } else {
          while (!buy.isEmpty() && amount > 0 && buy.peek().price >= price) {
            Order buyOrder = buy.poll();
            int matched = Math.min(amount, buyOrder.amount);
            amount -= matched;
            buyOrder.amount -= matched;
            if (buyOrder.amount > 0) {
              buy.offer(buyOrder);
            }
          }
        }
      }

      return 0;
    }
  }

  @Getter
  @Setter
  @AllArgsConstructor
  class Employee {
    String name;
    String dept;
    int salary;
  }

  public static void main(String[] args) {

    List<Employee> employees = new ArrayList<>();

    Map<String, List<Employee>> result =
        employees.stream().collect(Collectors.groupingBy(e -> e.getDept()));

    List<String> names = Arrays.asList("John", "David", "James", "Daniel", "Mark");

    Map<Integer, List<String>> res = names.stream().collect(Collectors.groupingBy(String::length));
    System.out.println(res);

    // Grouping and counting
    Map<String, Long> res2 =
        names.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    System.out.println(res2);

    List<String> names2 = Arrays.asList("John", "David", "Mike");
    List<String> toupper = names2.stream().map(s -> s.toUpperCase()).collect(Collectors.toList());
    System.out.println(toupper);

    List<List<Integer>> numbers =
        Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5, 6));

    List<Integer> ls = numbers.stream().flatMap(List::stream).collect(Collectors.toList());
    System.out.println(ls);
  }

}
