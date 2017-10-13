package feature.document.steps.constants;

/**
 * StepsRegex contains constants for chunks of step definitions to be used in feature files
 *
 * @author gd <g.dimartino@reply.it>
 */
public class StepsRegex {
    public static final String CHANNELS = "(ADAirport|ADCustomerService|Digital|PublicApiMobile|PublicApiB2B)";
    public static final String CHANNEL_LIST = "((?:ADAirport(?:,\\s)?)?(?:ADCustomerService(?:,\\s)?)?(?:Digital(?:,\\s)?)?(?:PublicApiMobile(?:,\\s)?)?(?:PublicApiB2B)?)";

    public static final String FARE_TYPES = "(Standard|Flexi|Staff|Standby|Inclusive)";
    public static final String PASSENGER_TYPES = "(adult|child|infant|infantOnLap|infantOnSeat)";

    public static final String RETURN = "( with return)?";
    public static final String SECTOR = "(?:(?:(?: (?:from|with|for) (\\w{3}|same|different|apis|non-apis|DCS|non-DCS)))?(?: (?:to )?(\\w{3}|sector))?)?";
    public static final String FARE_TYPE = "(?: with (Standard|Flexi|Staff|Standby|Inclusive) fare)?";
    public static final String FARE_TYPE_LIST = "(?: with ((?:Standard(?:,\\s)?)?(?:Flexi(?:,\\s)?)?(?:Staff(?:,\\s)?)?(?:Standby(?:,\\s)?)?(?:Inclusive(?:,\\s)?)?(?:Group)?) fares?)?";
    public static final String PASSENGER_MIX = "(?: for (\\d(?:,\\d)?\\s+\\w+(?:\\s*;\\s*\\d(?:,\\d)?\\s+\\w+)*))?";
    public static final String PASSENGER_MIX_APIS = "(?: for (\\d(?:,\\d)?\\s+\\w+(?:\\s*;\\s*\\d(?:,\\d)?\\s+\\w+)*)( without apis data)?)?";
    public static final String DATES = "(?:(?: departing (today|in \\d+ days?))?(?:(?: and)? returning after (\\d+) days?)?)?";
    public static final String FLEXIBLE_DAYS = "(?: with (\\d+) flexible days)?";
    public static final String DEAL = "(?: using (.+) application id and (.+) office id(?: and (.+) corporate id)? deal information)?";
    public static final String CURRENCY = "(?: with (\\w{3}) currency)?";
    public static final String JOURNEY = "(?: as an? (outbound|inbound|single|outbound/inbound) journey)?";
    public static final String CHECK_IN = "( for checked-in passengers)?";
    public static final String HOLD_ITEM = "(?: an| with)?(?: (\\d+))? hold bags?(?: with (\\d+) (.+)? excess weight)?(?: for (\\d?|each) passenger)?";
    public static final String HOLD_ITEMS = "((?: an| with)?(?: (\\d+))? hold bags?(?: with (\\d+) (.+)? excess weight)?(?: for (\\d?|each) passenger)?)?";
    public static final String SPORTS_ITEM = "(?: and)?(?: a| with)?(?: (\\d+))? sport items?(?: for (\\d?|each) passenger)?";
    public static final String SPORTS_ITEMS = "((?: and)?(?: a| with)?(?: (\\d+))? sport items?(?: for (\\d?|each) passenger)?)?";

}