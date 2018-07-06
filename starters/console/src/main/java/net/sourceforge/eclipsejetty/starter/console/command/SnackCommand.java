// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package net.sourceforge.eclipsejetty.starter.console.command;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.console.util.WordWrap;

/**
 * Eats snacks.
 * 
 * @author Manfred Hantschel
 */
public class SnackCommand extends AbstractCommand
{

    private static final String[] ANSWERS = { //
        "Thank you {user}, for the {snack}! I really like {snacks}.", //
            "Yummy! How did you know that {snack} [is|are] my favorite food?", //
            "I don't know, how I could ever live without [{a}|] {snack}! Thank you, {user}.", //
            "Have YOU ever eaten {snacks}?", //
            "No thanks, had {snacks} for breakfast.", //
            "The {snack} [is|are] a bit smelly.", //
            "Did you know, eating {snacks} can cause nervous tics?", //
            "Where did you find the {snack}? Under your keyboard?", //
            "I had [{a}|] {snack} once. Got stuck in my teeth.", //
            "Did the {snack} had close contact with your underwear?", // 
            "And now I have to ask myself, 'Would Godzilla eat [{a}|] {snack}?'", //
            "Do all {snacks} glow greenish? And what's this? [Is|Are] the {snack} growing eyes?", //
            "[Isn't|Aren't] the {snack} looking a bit antiquarian? [Is's|They're] growing hair!", //
            "[Does|Do] the {snack} have a best before date? It seems to try hiding from me!", //
            "Put [that|these] {snack} away, {user}! Chuck Norris was eaten by [{a}|] {snack}.", //
            "I hope the {snack} [does|do] not attack me!", //
            "Good. Out of the door, line on the left, one {snack} each.", //
            "Cut down a tree with [{a}|] {snack}? It can't be done.", //
            "Indeed, this [is|are] [{a}|] tasty {snack}!", // 
            "Praise the holy {snack}!", //
            "{User}, do you know what they call [{a}|] {snack} with cheese in Paris?", //
            "A little more caution from you; that [is|are] no {snack} you carry.", //
            "Nobody's looking for [{a}|] {snack} in today's wintry economic climate.", //
            "I love the smell of {snack} in the morning...", //
            "And in the morning, I'm making {snack}!",};

    private static final String VOCALS = "aeiou";

    public SnackCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "snack", "give");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getFormat()
     */
    public String getFormat()
    {
        return "[something]";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getDescription()
     */
    public String getDescription()
    {
        return "Give your Jetty a snack.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.AbstractCommand#getHelpDescription()
     */
    @Override
    protected String getHelpDescription()
    {
        return "Give your Jetty a snack. I hope it likes it.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getOrdinal()
     */
    public int getOrdinal()
    {
        return -1;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#execute(java.lang.String,
     *      net.sourceforge.eclipsejetty.starter.console.Process)
     */
    public int execute(String commandName, Process process)
    {
        StringBuilder builder = new StringBuilder();

        for (String arg : process.args)
        {
            if (builder.length() > 0)
            {
                builder.append(" ");
            }

            builder.append(arg);
        }

        String snack = builder.toString();

        if (snack.length() == 0)
        {
            snack = "snack";
        }

        String answer = ANSWERS[checksum(snack) % ANSWERS.length];

        answer = replace(answer, "user", uppercase(System.getProperty("user.name")));

        boolean plural = isPlural(snack);

        if (plural)
        {
            answer = choice(answer, 1);
            answer = replace(answer, "snack", snack);
            answer = replace(answer, "snacks", snack);
        }
        else
        {
            boolean startsWithVocal = isVocal(snack.charAt(0));

            answer = choice(answer, 0);

            if (startsWithVocal)
            {
                answer = replace(answer, "a", "an");
            }
            else
            {
                answer = replace(answer, "a", "a");
            }

            answer = replace(answer, "snack", snack);
            answer = replace(answer, "snacks", toPlural(snack));
        }

        process.out.println(new WordWrap().perform(answer.replaceAll(" +", " "), consoleAdapter.getLineLength()));

        return 0;
    }

    private boolean isVocal(char c)
    {
        return VOCALS.indexOf(Character.toLowerCase(c)) >= 0;
    }

    private boolean isVocal(String s, int index)
    {
        if ((index < 0) || (index >= s.length()))
        {
            return false;
        }

        return isVocal(s.charAt(index));
    }

    private String cut(String s, int index)
    {
        if ((index < 0) || (index >= s.length()))
        {
            return s;
        }

        return s.substring(0, index);
    }

    private boolean isPlural(String s)
    {
        if (s.toLowerCase().endsWith("ss"))
        {
            return false;
        }

        return endsWith(s.toLowerCase(), "s", "men", "mice", "feet", "teeth");
    }

    private String toPlural(String s)
    {
        if (s.toLowerCase().endsWith("man"))
        {
            return s.substring(0, s.length() - 2) + "en";
        }

        if (s.toLowerCase().endsWith("mouse"))
        {
            return s.substring(0, s.length() - 4) + "ice";
        }

        if (s.toLowerCase().endsWith("foot"))
        {
            return s.substring(0, s.length() - 3) + "eet";
        }

        if (s.toLowerCase().endsWith("tooth"))
        {
            return s.substring(0, s.length() - 4) + "eeth";
        }

        if (s.toLowerCase().endsWith("y"))
        {
            if (!isVocal(s, s.length() - 2))
            {
                return s + "s";
            }

            return cut(s, s.length() - 1) + "ies";
        }

        if (endsWith(s.toLowerCase(), "ch", "s", "sh", "x", "z"))
        {
            return s + "es";
        }

        if ((s.toLowerCase().endsWith("f")) && (!s.toLowerCase().endsWith("ff")))
        {
            return s.substring(0, s.length() - 1) + "ves";
        }

        if (s.toLowerCase().endsWith("fe"))
        {
            return s.substring(0, s.length() - 2) + "ves";
        }

        return s + "s";
    }

    private boolean endsWith(String s, String... endings)
    {
        for (String ending : endings)
        {
            if (s.endsWith(ending))
            {
                return true;
            }
        }

        return false;
    }

    private String replace(String s, String pattern, String replacement)
    {
        s = s.replace("{" + pattern + "}", replacement);
        s = s.replace("{" + uppercase(pattern) + "}", uppercase(replacement));

        return s;
    }

    private String uppercase(String s)
    {
        if (s.length() <= 0)
        {
            return s;
        }

        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String choice(String s, int choice)
    {
        StringBuilder builder = new StringBuilder();

        int start = 0;

        while (true)
        {
            int index = s.indexOf('[', start);

            if (index < 0)
            {
                builder.append(s.substring(start));

                return builder.toString();
            }
            else
            {
                builder.append(s.substring(start, index));

                String[] choices = s.substring(index + 1, s.indexOf(']', start)).split("\\|");

                if (choice < choices.length)
                {
                    builder.append(choices[choice]);
                }

                start = s.indexOf(']', start) + 1;
            }
        }
    }

    private static int checksum(String s)
    {
        int sum = 0;

        for (int i = 0; i < s.length(); i += 1)
        {
            sum = (sum << 1) + s.charAt(i);
        }

        return sum;
    }
}
