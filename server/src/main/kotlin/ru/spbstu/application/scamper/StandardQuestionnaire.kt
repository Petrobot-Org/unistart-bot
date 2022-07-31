package ru.spbstu.application.scamper

import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.telegram.IdeaGenerationStrings

val StandardQuestionnaire = Questionnaire(
    listOf(
        ScamperLetter(
            character = 'S',
            description = IdeaGenerationStrings.SDescription,
            questions = IdeaGenerationStrings.SQuestions,
            bonusType = BonusType.ScamperS
        ),
        ScamperLetter(
            character = 'C',
            description = IdeaGenerationStrings.CDescription,
            questions = IdeaGenerationStrings.СQuestions,
            bonusType = BonusType.ScamperC
        ),
        ScamperLetter(
            character = 'A',
            description = IdeaGenerationStrings.ADescription,
            questions = IdeaGenerationStrings.AQuestions,
            bonusType = BonusType.ScamperA
        ),
        ScamperLetter(
            character = 'M',
            description = IdeaGenerationStrings.MDescription,
            questions = IdeaGenerationStrings.MQuestions,
            bonusType = BonusType.ScamperM
        ),
        ScamperLetter(
            character = 'P',
            description = IdeaGenerationStrings.PDescription,
            questions = IdeaGenerationStrings.PQuestions,
            bonusType = BonusType.ScamperP
        ),
        ScamperLetter(
            character = 'E',
            description = IdeaGenerationStrings.EDescription,
            questions = IdeaGenerationStrings.EQuestions,
            bonusType = BonusType.ScamperE
        ),
        ScamperLetter(
            character = 'R',
            description = IdeaGenerationStrings.RDescription,
            questions = IdeaGenerationStrings.RQuestions,
            bonusType = BonusType.ScamperR
        ),
    )
)
