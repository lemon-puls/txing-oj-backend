package com.bitdf.txing.oj;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.lemon.util.service.TableGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest
public class CommonTest {
    @Autowired
    TableGenerator tableGenerator;

    @Test
    void createTable() throws SQLException {
        tableGenerator.generateTable();
    }
}
