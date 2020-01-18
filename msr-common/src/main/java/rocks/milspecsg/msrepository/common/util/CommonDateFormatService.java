/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msrepository.common.util;

import rocks.milspecsg.msrepository.api.util.DateFormatService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class CommonDateFormatService implements DateFormatService {

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    @Override
    public String format(Date date) {
        return df.format(date);
    }

    @Override
    public String formatDiff(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        StringBuilder s = new StringBuilder();
        int years = calendar.get(Calendar.YEAR) - 1970;
        int months = calendar.get(Calendar.MONTH);
        int days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        int hours = calendar.get(Calendar.HOUR_OF_DAY) - 1;
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        if (years > 0) {
            s.append(years).append(years == 1 ? " year, " : " years, ");
        }
        if (months > 0) {
            s.append(months).append(months == 1 ? " month, " : " months, ");
        }
        if (days > 0) {
            s.append(days).append(days == 1 ? " day, " : " days, ");
        }
        if (hours > 0) {
            s.append(hours).append(hours == 1 ? " hour, " : " hours, ");
        }
        if (minutes > 0) {
            s.append(minutes).append(minutes == 1 ? " minute, " : " minutes, ");
        }
        if (seconds > 0) {
            s.append(seconds).append(seconds == 1 ? " second, " : " seconds");
        }
        if (s.substring(s.length() - 2, s.length()).equals(", ")) {
            s.deleteCharAt(s.length() - 1);
            s.deleteCharAt(s.length() - 1);
        }
        return s.toString();
    }

    @Override
    public Date parseUnsafe(String date) throws ParseException {
        return df.parse(date);
    }

    @Override
    public Optional<Date> parse(String date) {
        Date result;
        try {
            result = parseUnsafe(date);
        } catch (ParseException e) {
            return Optional.empty();
        }
        return Optional.of(result);
    }
}
