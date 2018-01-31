/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbb.demetra.model.rest.utils;

import ec.tss.tsproviders.utils.IParser;
import ec.tss.tsproviders.utils.Parsers;
import ec.util.spreadsheet.Cell;
import ec.util.spreadsheet.Sheet;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Mats
 */
public class SheetAdapter extends Sheet {

    private Object[][] range;

    public SheetAdapter(Object[][] source) {
        super();
        this.range = source;
    }

    @Override
    public int getRowCount() {
        if (range == null) {
            return 0;
        }
        return range.length;
    }

    @Override
    public int getColumnCount() {
        if (range == null || range[0] == null) {
            return 0;
        }
        return range[0].length;
    }

    @Override
    public Cell getCell(int row, int col) throws IndexOutOfBoundsException {
        Object value = range[row][col];
        return new CellImpl(value);
    }

    @Override
    public String getName() {
        return "MySheet";
    }

    @lombok.RequiredArgsConstructor
    private class CellImpl extends Cell {

        private final Object value;
        private final IParser<Date> dateParser = Parsers.onStrictDatePattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT);

        @Override
        public double getDouble() throws UnsupportedOperationException {
            return super.getDouble();
        }

        @Override
        public Object getValue() {
            return super.getValue();
        }

        @Override
        public Type getType() {
            return super.getType();
        }

        @Override
        public boolean isDate() {
            return value != null && dateParser.parseValue(value.toString()).isPresent();
        }

        @Override
        public boolean isString() {
            return value instanceof String;
        }

        @Override
        public boolean isNumber() {
            return value instanceof Double;
        }

        @Override
        public Number getNumber() throws UnsupportedOperationException {
            return (Double) value;
        }

        @Override
        public Date getDate() throws UnsupportedOperationException {
            return dateParser.parse(value.toString());
        }

        @Override
        public String getString() throws UnsupportedOperationException {
            return (String)value;
        }
    }
}
