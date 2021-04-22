package com.tngtech.jgiven.report.analysis;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultiset;
import com.tngtech.jgiven.impl.util.AssertionUtil;
import com.tngtech.jgiven.report.model.AttachmentModel;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analyzes a report model and tries to infer which step method arguments match to which case argument.
 *
 * This is done by comparing all cases of a scenario and find out which method arguments
 * match in all cases to the same parameter.
 *
 * The algorithm is rather complex, but I could not find an easier one yet.
 */
public class CaseArgumentAnalyser {
    private static final Logger log = LoggerFactory.getLogger(CaseArgumentAnalyser.class);

    public void analyze(ReportModel model) {
        for (ScenarioModel scenarioModel : model.getScenarios()) {
            analyze(scenarioModel);
        }
    }

    static class JoinedArgs {
        final List<Word> words;

        public JoinedArgs(Word word) {
            words = Lists.newArrayList(word);
        }
    }

    public void analyze(ScenarioModel scenarioModel) {
        if (scenarioModel.getScenarioCases().size() == 1) {
            return;
        }

        if (!isStructuralIdentical(scenarioModel)) {
            log.debug("Cases are structurally different, cannot create data table");
            return;
        }
        scenarioModel.setCasesAsTable(true);

        // get all words that are arguments
        List<List<Word>> argumentWords = collectArguments(scenarioModel);
        AssertionUtil.assertFalse(argumentCountDiffer(argumentWords), "Argument count differs");

        // filter out arguments that are the same in all cases
        // only keep arguments that actually differ between cases
        List<List<Word>> differentArguments = getDifferentArguments(argumentWords);

        // now join arguments that are the same within each case
        List<List<JoinedArgs>> joinedArgs = joinEqualArguments(differentArguments);

        // finally we try to use the parameter names of the scenario
        List<List<String>> explicitParameterValues = getExplicitParameterValues(scenarioModel.getScenarioCases());
        List<String> argumentNames =
            findArgumentNames(joinedArgs, explicitParameterValues, scenarioModel.getExplicitParameters());

        List<List<Word>> arguments = getFirstWords(joinedArgs);

        setParameterNames(joinedArgs, argumentNames);
        scenarioModel.setDerivedParameters(argumentNames);

        for (int caseCounter = 0; caseCounter < arguments.size(); caseCounter++) {
            scenarioModel.getCase(caseCounter).setDerivedArguments(getFormattedValues(arguments.get(caseCounter)));
        }
    }

    private List<List<String>> getExplicitParameterValues(List<ScenarioCaseModel> scenarioCases) {
        List<List<String>> explicitParameterValues = Lists.newArrayListWithExpectedSize(scenarioCases.size());

        for (ScenarioCaseModel caseModel : scenarioCases) {
            explicitParameterValues.add(caseModel.getExplicitArguments());
        }

        return explicitParameterValues;
    }

    /**
     * Finds for each JoinedArgs set the best fitting name.
     * <p>
     * First it is tried to find a name from the explicitParameterNames list, by comparing the argument values
     * with the explicit case argument values. If no matching value can be found, the name of the argument is taken.
     */
    private List<String> findArgumentNames(List<List<JoinedArgs>> joinedArgs,
                                           List<List<String>> explicitParameterValues,
                                           List<String> explicitParameterNames) {
        List<String> argumentNames = Lists.newArrayListWithExpectedSize(joinedArgs.get(0).size());
        Multiset<String> paramNames = TreeMultiset.create();

        arguments:
        for (int argumentCounter = 0; argumentCounter < joinedArgs.get(0).size(); argumentCounter++) {
            parameters:
            for (int paramCounter = 0; paramCounter < explicitParameterNames.size(); paramCounter++) {
                String paramName = explicitParameterNames.get(paramCounter);

                boolean formattedValueMatches = true;
                boolean valueMatches = true;
                for (int caseCounter = 0; caseCounter < joinedArgs.size(); caseCounter++) {
                    JoinedArgs args = joinedArgs.get(caseCounter).get(argumentCounter);

                    String parameterValue = explicitParameterValues.get(caseCounter).get(paramCounter);

                    String formattedValue = args.words.get(0).getFormattedValue();
                    if (!formattedValue.equals(parameterValue)) {
                        formattedValueMatches = false;
                    }

                    String value = args.words.get(0).getValue();
                    if (!value.equals(parameterValue)) {
                        valueMatches = false;
                    }

                    if (!formattedValueMatches && !valueMatches) {
                        continue parameters;
                    }
                }

                // on this point either all formatted values match or all values match (or both)
                argumentNames.add(paramName);
                paramNames.add(paramName);
                continue arguments;
            }

            argumentNames.add(null);
        }

        Set<String> usedNames = Sets.newHashSet();
        for (int argumentCounter = 0; argumentCounter < joinedArgs.get(0).size(); argumentCounter++) {
            String name = argumentNames.get(argumentCounter);
            if (name == null || paramNames.count(name) > 1) {
                String origName = getArgumentName(joinedArgs, argumentCounter);
                name = findFreeName(usedNames, origName);
                argumentNames.set(argumentCounter, name);
            }
            usedNames.add(name);

        }

        return argumentNames;
    }

    private String getArgumentName(List<List<JoinedArgs>> joinedArgs, int argumentCounter) {
        return joinedArgs.get(0).get(argumentCounter).words.get(0).getArgumentInfo().getArgumentName();
    }

    private String findFreeName(Set<String> usedNames, String origName) {
        String name = origName;
        int counter = 2;
        while (usedNames.contains(name)) {
            name = origName + counter;
            counter++;
        }
        usedNames.add(name);
        return name;
    }

    private List<List<Word>> getFirstWords(List<List<JoinedArgs>> joinedArgs) {
        List<List<Word>> result = Lists.newArrayList();
        for (int i = 0; i < joinedArgs.size(); i++) {
            result.add(Lists.newArrayList());
        }

        for (int i = 0; i < joinedArgs.size(); i++) {
            for (int j = 0; j < joinedArgs.get(i).size(); j++) {
                result.get(i).add(joinedArgs.get(i).get(j).words.get(0));
            }
        }

        return result;
    }

    List<List<JoinedArgs>> joinEqualArguments(List<List<Word>> differentArguments) {
        List<List<JoinedArgs>> joined = Lists.newArrayList();
        for (int i = 0; i < differentArguments.size(); i++) {
            joined.add(Lists.newArrayList());
        }

        if (differentArguments.get(0).isEmpty()) {
            return joined;
        }

        for (int caseCounter = 0; caseCounter < differentArguments.size(); caseCounter++) {
            joined.get(caseCounter).add(new JoinedArgs(differentArguments.get(caseCounter).get(0)));
        }

        int numberOfArgs = differentArguments.get(0).size();

        outer:
        for (int i = 1; i < numberOfArgs; i++) {
            inner:
            for (int j = 0; j < joined.get(0).size(); j++) {

                for (int caseCounter = 0; caseCounter < differentArguments.size(); caseCounter++) {
                    Word newWord = differentArguments.get(caseCounter).get(i);
                    Word joinedWord = joined.get(caseCounter).get(j).words.get(0);

                    if (!newWord.getFormattedValue().equals(joinedWord.getFormattedValue())) {
                        continue inner;
                    }
                }

                for (int caseCounter = 0; caseCounter < differentArguments.size(); caseCounter++) {
                    joined.get(caseCounter).get(j).words.add(differentArguments.get(caseCounter).get(i));
                }

                continue outer;
            }

            for (int caseCounter = 0; caseCounter < differentArguments.size(); caseCounter++) {
                joined.get(caseCounter).add(new JoinedArgs(differentArguments.get(caseCounter).get(i)));
            }
        }

        return joined;
    }

    /**
     * A scenario model is structural identical if all cases have exactly the same
     * steps, except for values of step arguments.
     * <p>
     * This is implemented by comparing all cases with the first one
     */
    private boolean isStructuralIdentical(ScenarioModel scenarioModel) {
        ScenarioCaseModel firstCase = scenarioModel.getScenarioCases().get(0);

        for (int caseCounter = 1; caseCounter < scenarioModel.getScenarioCases().size(); caseCounter++) {
            ScenarioCaseModel caseModel = scenarioModel.getScenarioCases().get(caseCounter);
            if (stepsAreDifferent(firstCase, caseModel)) {
                return false;
            }
        }

        return true;
    }

    boolean stepsAreDifferent(ScenarioCaseModel firstCase, ScenarioCaseModel secondCase) {
        return stepsAreDifferent(firstCase.getSteps(), secondCase.getSteps());
    }

    boolean stepsAreDifferent(List<StepModel> firstSteps, List<StepModel> secondSteps) {
        if (firstSteps.size() != secondSteps.size()) {
            return true;
        }

        for (int stepCounter = 0; stepCounter < firstSteps.size(); stepCounter++) {
            StepModel firstStep = firstSteps.get(stepCounter);
            StepModel secondStep = secondSteps.get(stepCounter);

            if (firstStep.getWords().size() != secondStep.getWords().size()) {
                return true;
            }

            if (attachmentsAreStructurallyDifferent(firstStep.getAttachments(), secondStep.getAttachments())) {
                return true;
            }

            if (wordsAreDifferent(firstStep, secondStep)) {
                return true;
            }

            if (stepsAreDifferent(firstStep.getNestedSteps(), secondStep.getNestedSteps())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attachments are only structurally different if one step has an inline attachment
     * and the other step either has no inline attachment or the inline attachment is
     * different.
     */
    boolean attachmentsAreStructurallyDifferent(List<AttachmentModel> firstAttachments,
                                                List<AttachmentModel> otherAttachments) {
        if (firstAttachments.size() != otherAttachments.size()) {
            return true;
        }

        for (int i = 0; i < firstAttachments.size(); i++) {
            if (attachmentIsStructurallyDifferent(firstAttachments.get(i), otherAttachments.get(i))) {
                return true;
            }
        }
        return false;
    }

    boolean attachmentIsStructurallyDifferent(AttachmentModel firstAttachment, AttachmentModel otherAttachment) {
        if (isInlineAttachment(firstAttachment) != isInlineAttachment(otherAttachment)) {
            return true;
        }

        if (isInlineAttachment(firstAttachment)) {
            return !firstAttachment.getValue().equals(otherAttachment.getValue());
        }

        return false;
    }

    private boolean isInlineAttachment(AttachmentModel attachmentModel) {
        return attachmentModel != null && attachmentModel.isShowDirectly();
    }

    private boolean wordsAreDifferent(StepModel firstStep, StepModel stepModel) {
        for (int wordCounter = 0; wordCounter < firstStep.getWords().size(); wordCounter++) {
            Word firstWord = firstStep.getWord(wordCounter);
            Word word = stepModel.getWord(wordCounter);

            if (firstWord.isArg() != word.isArg()) {
                return true;
            }

            if (!firstWord.isArg() && !firstWord.getValue().equals(word.getValue())) {
                return true;
            }

            if (firstWord.isArg() && firstWord.isDataTable()
                && !firstWord.getArgumentInfo().getDataTable().equals(word.getArgumentInfo().getDataTable())) {
                return true;
            }

        }
        return false;
    }

    private void setParameterNames(List<List<JoinedArgs>> differentArguments, List<String> argumentNames) {
        AssertionUtil
            .assertTrue(argumentNames.size() == differentArguments.get(0).size(), "Number of argument names is wrong");

        for (int argumentCounter = 0; argumentCounter < argumentNames.size(); argumentCounter++) {
            for (List<JoinedArgs> differentArgument : differentArguments) {
                for (Word word : differentArgument.get(argumentCounter).words) {
                    word.getArgumentInfo().setParameterName(argumentNames.get(argumentCounter));
                }
            }
        }
    }

    private List<String> getFormattedValues(List<Word> words) {
        List<String> formattedValues = Lists.newArrayListWithExpectedSize(words.size());
        for (Word word : words) {
            formattedValues.add(word.getFormattedValue());
        }
        return formattedValues;
    }

    /**
     * Returns a list with argument words that are not equal in all cases.
     */
    List<List<Word>> getDifferentArguments(List<List<Word>> argumentWords) {
        List<List<Word>> result = Lists.newArrayList();
        for (int i = 0; i < argumentWords.size(); i++) {
            result.add(Lists.newArrayList());
        }

        int numberOfWords = argumentWords.get(0).size();

        for (int wordCounter = 0; wordCounter < numberOfWords; wordCounter++) {
            Word wordOfFirstCase = argumentWords.get(0).get(wordCounter);

            // data tables have equal here, otherwise
            // the cases would be structurally different
            if (wordOfFirstCase.isDataTable()) {
                continue;
            }

            boolean different = false;
            for (int caseCounter = 1; caseCounter < argumentWords.size(); caseCounter++) {
                Word wordOfCase = argumentWords.get(caseCounter).get(wordCounter);
                if (!wordOfCase.getFormattedValue().equals(wordOfFirstCase.getFormattedValue())) {
                    different = true;
                    break;
                }

            }
            if (different) {
                for (int caseCounter = 0; caseCounter < argumentWords.size(); caseCounter++) {
                    result.get(caseCounter).add(argumentWords.get(caseCounter).get(wordCounter));
                }
            }
        }

        return result;
    }

    List<List<Word>> collectArguments(ScenarioModel scenarioModel) {
        List<List<Word>> argumentWords = Lists.newArrayList();

        for (ScenarioCaseModel scenarioCaseModel : scenarioModel.getScenarioCases()) {
            argumentWords.add(findArgumentWords(scenarioCaseModel));
        }
        return argumentWords;
    }

    private boolean argumentCountDiffer(List<List<Word>> argumentWords) {
        int numberOfArguments = argumentWords.get(0).size();

        for (int i = 1; i < argumentWords.size(); i++) {
            if (argumentWords.get(i).size() != numberOfArguments) {
                return true;
            }
        }
        return false;
    }

    private List<Word> findArgumentWords(ScenarioCaseModel scenarioCaseModel) {
        List<Word> arguments = Lists.newArrayList();
        List<StepModel> steps = scenarioCaseModel.getSteps();
        findArgumentWords(steps, arguments);
        return arguments;
    }

    private void findArgumentWords(List<StepModel> steps, List<Word> arguments) {
        for (StepModel step : steps) {
            for (Word word : step.getWords()) {
                if (word.isArg()) {
                    arguments.add(word);
                }
            }
            findArgumentWords(step.getNestedSteps(), arguments);
        }
    }
}
