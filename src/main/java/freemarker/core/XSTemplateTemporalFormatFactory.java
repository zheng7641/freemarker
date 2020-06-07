/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package freemarker.core;

import static freemarker.core.ISOTemplateTemporalFormatFactory.*;

import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.TimeZone;

class XSTemplateTemporalFormatFactory extends TemplateTemporalFormatFactory {

    static final XSTemplateTemporalFormatFactory INSTANCE = new XSTemplateTemporalFormatFactory();

    private XSTemplateTemporalFormatFactory() {
        // Not meant to be called from outside
    }

    private final static DateTimeFormatter XSD_DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .optionalStart()
            .appendLiteral('T')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(":")
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(":")
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
            .optionalEnd()
            .optionalStart()
            .appendOffsetId()
            .optionalEnd()
            .toFormatter()
            .withZone(ZoneOffset.UTC)
            .withLocale(Locale.US);

    private final static DateTimeFormatter XSD_TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(":")
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(":")
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
            .optionalStart()
            .appendOffsetId()
            .optionalEnd()
            .toFormatter()
            .withZone(ZoneOffset.UTC)
            .withLocale(Locale.US);

    private static final DateTimeFormatter XSD_YEARMONTH_FORMAT = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR)
            .appendLiteral("-")
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .optionalStart()
            .appendOffsetId()
            .optionalEnd()
            .toFormatter()
            .withZone(ZoneOffset.UTC)
            .withLocale(Locale.US);

    @Override
    public TemplateTemporalFormat get(String params, Class<? extends Temporal> temporalClass, Locale locale, TimeZone timeZone, Environment env) throws
            TemplateValueFormatException {
        if (!params.isEmpty()) {
            // TODO [FREEMARKER-35]
            throw new InvalidFormatParametersException("xs currently doesn't support parameters");
        }

        return getXSFormatter(temporalClass, timeZone.toZoneId());
    }

    private static ISOLikeTemplateTemporalFormat getXSFormatter(Class<? extends Temporal> temporalClass, ZoneId timeZone) {
        final DateTimeFormatter dateTimeFormatter;
        final String description;
        if (temporalClass == LocalTime.class) {
            dateTimeFormatter = XSD_TIME_FORMAT;
            description = "W3C XML Schema time";
        } else if (temporalClass == Year.class) {
            dateTimeFormatter = ISO8601_YEAR_FORMAT; // Same as ISO
            description = "W3C XML Schema year";
        } else if (temporalClass == YearMonth.class) {
            dateTimeFormatter = XSD_YEARMONTH_FORMAT;
            description = "W3C XML Schema year-month";
        } else {
            Class<? extends Temporal> normTemporalClass =
                    _CoreTemporalUtils.normalizeSupportedTemporalClass(temporalClass);
            if (normTemporalClass != temporalClass) {
                return getXSFormatter(normTemporalClass, timeZone);
            } else {
                dateTimeFormatter = XSD_DATE_TIME_FORMAT;
                description = "W3C XML Schema date-time";
            }
        }
        // TODO [FREEMARKER-35] What about date-only?
        return new ISOLikeTemplateTemporalFormat(dateTimeFormatter.withZone(timeZone), description);
    }

}
