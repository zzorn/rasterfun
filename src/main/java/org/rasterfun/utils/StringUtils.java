package org.rasterfun.utils;

/**
 *
 */
public final class StringUtils {

    /**
     * @return a valid java identifier, generated from the user readable name.
     */
    public static String identifierFromName(String userReadableName) {
        return identifierFromName(userReadableName, '_');
    }

    /**
     * @return a valid java identifier, generated from the user readable name.
     * If the identifier contains a non valid identifier character, it is replaced with the fillCharacter.
     */
    public static String identifierFromName(String userReadableName, char fillCharacter) {
        assert Character.isJavaIdentifierStart(fillCharacter) : "Fill character must be a valid java identifier start & part, but it was '" + fillCharacter + "'";

        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = false;
        boolean firstCreated = false;
        for (int i = 0; i < userReadableName.length(); i++) {
            char c = userReadableName.charAt(i);

            // Skip spaces, capitalize character after space
            if (c == ' ') capitalizeNext = true;
            else {
                // Capitalize if needed
                if (capitalizeNext) {
                    c = Character.toUpperCase(c);
                    capitalizeNext = false;
                }

                if (!firstCreated) {
                    // Handle first character
                    if (Character.isJavaIdentifierStart(c)) {
                        // Valid, just add it
                        sb.append(c);
                    } else {
                        // Invalid, add fill char instead
                        sb.append(fillCharacter);

                        // Add the character if it was a valid part but not start (e.g. number)
                        if (Character.isJavaIdentifierPart(c)) sb.append(c);
                    }
                    firstCreated = true;
                }
                else {
                    if (Character.isJavaIdentifierPart(c)) sb.append(c);
                    else sb.append(fillCharacter);
                }
            }
        }

        // Make sure it is at least one char long
        if (sb.length() <= 0) sb.append(fillCharacter);

        return sb.toString();
    }


    /**
     * @return number of times the character c is found in the string s.
     */
    public static int countCharacters(String s, char c) {
        int sum = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) sum++;
        }
        return sum;
    }


}
