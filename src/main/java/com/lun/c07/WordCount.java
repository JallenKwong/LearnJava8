package com.lun.c07;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class WordCount {

    public static final String SENTENCE =
            " Nel   mezzo del cammin  di nostra  vita " +
            "mi  ritrovai in una  selva oscura" +
            " che la  dritta via era   smarrita ";

    public static void main(String[] args) {
        System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
        System.out.println("Found " + countWords(SENTENCE) + " words");
    }

    public static int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else {
                if (lastSpace) counter++;
                lastSpace = Character.isWhitespace(c);
            }
        }
        return counter;
    }

    public static int countWords(String s) {
//        Stream<Character> stream = IntStream.range(0, s.length())
//                                            .mapToObj(SENTENCE::charAt).parallel();
        Spliterator<Character> spliterator = new WordCounterSpliterator(s);
        Stream<Character> stream = StreamSupport.stream(spliterator, true);

        return countWords(stream);
    }

    private static int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
                                                WordCounter::accumulate,
                                                WordCounter::combine);
        return wordCounter.getCounter();
    }

    private static class WordCounter {
        private final int counter;
        private final boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        /**
         * accumulate方法定义了如何更改WordCounter的状态，或更确切地说是用<br>
         * 哪个状态来建立新的WordCounter，因为这个类是不可变的。每次遍历到Stream中的一个新的<br>
         * Character时，就会调用accumulate方法。当上一个字符是空格，新字符不是空格时，计数器就加一。
         * 
         * @param c
         * @return
         */
        public WordCounter accumulate(Character c) {
            if (Character.isWhitespace(c)) {
                return lastSpace ? this : new WordCounter(counter, true);
            } else {
                return lastSpace ? new WordCounter(counter+1, false) : this;
            }
        }

        /**
         * 调用第二个方法combine 时， 会对作用于Character 流的两个不同子部分的两个<br>
         * WordCounter的部分结果进行汇总，也就是把两个WordCounter内部的计数器加起来
         * 
         * @param wordCounter
         * @return
         */
        public WordCounter combine(WordCounter wordCounter) {
            return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
        }

        public int getCounter() {
            return counter;
        }
    }

    private static class WordCounterSpliterator implements Spliterator<Character> {

        private final String string;
        private int currentChar = 0;

        private WordCounterSpliterator(String string) {
            this.string = string;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
        	//处理当前字符
        	action.accept(string.charAt(currentChar++));
        	//如果还有字符要处理，则返回true
            return currentChar < string.length();
        }

        @Override
        public Spliterator<Character> trySplit() {
            int currentSize = string.length() - currentChar;
            if (currentSize < 10) {
                return null;//返回null表示要解析的String已经足够小，可以顺序处理
            }
            //将试探拆分位置设定为要解析的String的中间
            for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
                //让拆分位置前进直到下一个空格
            	if (Character.isWhitespace(string.charAt(splitPos))) {
            		//创建一个新WordCounterSpliterator来解析String从开始到拆分位置的部分
            		Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
            		//将这个WordCounter-Spliterator 的起始位置设为拆分位置
            		currentChar = splitPos;
                    return spliterator;
                }
            }
            return null;
        }

        @Override
        public long estimateSize() {
            return string.length() - currentChar;
        }

        @Override
        public int characteristics() {
            return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
        }
    }
}
