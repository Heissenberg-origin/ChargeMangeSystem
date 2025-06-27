package org.example.test;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.ArrangeInfo;

@Slf4j
public class Test1 {


        @org.junit.Test
        public void test() {
            ArrangeInfo arrangeInfo = new ArrangeInfo();
            log.info("{}", arrangeInfo);

        }
    }

