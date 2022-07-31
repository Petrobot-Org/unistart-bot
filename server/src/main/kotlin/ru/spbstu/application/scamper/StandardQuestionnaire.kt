package ru.spbstu.application.scamper

import ru.spbstu.application.telegram.IdeaGenerationStrings

val StandardQuestionnaire = Questionnaire(
    listOf(
        ScamperLetter(
            character = 'S',
            description = IdeaGenerationStrings.SDescription,
            questions = IdeaGenerationStrings.SQuestions
        ),
        ScamperLetter(
            character = 'C',
            description = IdeaGenerationStrings.CDescription,
            questions = IdeaGenerationStrings.СQuestions
        ),
        ScamperLetter(
            character = 'A',
            description = IdeaGenerationStrings.ADescription,
            questions = IdeaGenerationStrings.AQuestions
        ),
        ScamperLetter(
            character = 'M',
            description = IdeaGenerationStrings.MDescription,
            questions = IdeaGenerationStrings.MQuestions
        ),
        ScamperLetter(
            character = 'P',
            description = IdeaGenerationStrings.PDescription,
            questions = IdeaGenerationStrings.PQuestions
        ),
        ScamperLetter(
            character = 'E',
            description = IdeaGenerationStrings.EDescription,
            questions = IdeaGenerationStrings.EQuestions
        ),
        ScamperLetter(
            character = 'R',
            description = IdeaGenerationStrings.RDescription,
            questions = IdeaGenerationStrings.RQuestions
        ),
    )
)
