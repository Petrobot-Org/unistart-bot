package ru.spbstu.application.telegram

import ru.spbstu.application.steps.entities.IdeaGenerationInfo

object IdeaGenerationStrings {
    const val IdeaGenerationMethodsPath = "/static/ideaGeneration/"
    const val ChooseIdeaGeneration = "Выбери технику генерации идей"

    const val Bisociation = "Бисоциация"
    const val BisociationDescription =
        "*Бисоциация* – это комбинация из двух идей, взятых из разных контекстов, ранее казавшихся несовместимыми." +
                "Эффективность связанной с этим понятием креативной техники в том, что человек вынужден уйти от привычных схем мышления и устанавливать связи между, казалось бы, не связанными друг с другом понятиями."
    const val HowDoesBisociationWork =
        "Берется название предмета, подлежащего проектированию. Справа пишут 6-7 «слов-ответов», то есть существительных, произвольно взятых из словаря или подсказанных коллегами. Важно, чтобы в уме проектировщика эти слова не были связаны с предметом, который следует спроектировать."
    const val HeadingOfSystematization = "Рубрики систематизации"
    const val HeadingOfSystematizationInBisociation =
        "Для систематизации найденных решений их располагают под следующими рубриками:\n" +
                "\n" +
                "*СЕЙЧАС* - продукция или система может быть изготовлена немедленно\n" +
                "\n" +
                "*2-5 ЛЕТ* - концепция не совсем готова для производства\n" +
                "\n" +
                "*5-10 ЛЕТ*  - ответ ведет к долгосрочному планированию продукции или системы\n" +
                "\n" +
                "*Исследования и Разработки*  - решение представляется разумным, а его осуществимость должен определить отдел исследований и разработок\n" +
                "\n" +
                "*ТРЮКИ* - иногда в результате получается идея, которая не имеет ничего общего с самой продукцией, но дает новый рекламный трюк\n" +
                "\n" +
                "*ДРУГОЕ* - часто появляются идеи, которые вовсе не являются решениями данной проблемы дизайна. Тем не менее они могут стать новаторскими для решения проблем, находящихся за пределами исследования, и разработаны для других клиентов\n"
    const val BisociationPath = IdeaGenerationMethodsPath + "Bisociation.png"

    const val DelphiMethod = "Метод Дельфи"
    const val DelphiMethodDescription =
        "*Метод Дельфи* – это метод быстрого поиска решений, основанный на их генерации в процессе «мозговой атаки», проводимой группой специалистов, и отбора лучшего решения, исходя из экспертных оценок."
    const val HowDoesDelphiMethodWork =
        "Метод Дельфи позволяет проводить экспертное оценивание, главными его особенностями является *анонимность, заочность* и *многоуровневость*. При помощи определенных приемов, таких как мозговые штурмы, опросы или интервью, удается отыскать способ для определения правильного решения. То есть группа независимых друг от друга экспертов может гораздо лучше дать оценку той или иной ситуации, нежели структурированная группа людей. Поскольку участники могут даже не знать о существовании друг друга, – это полностью *исключает столкновение их интересов и мнений*."
    const val GroupsOfPeople = "Какие группы людей выделяются?"
    const val GroupsOfPeopleDelphiMethod = "В ходе использования данного метода принимают участие две группы людей:\n" +
            "1-я группа – это *эксперты*, имеющие свою точку зрения по какому-то вопросу.\n" +
            "2-я группа – это *аналитики*, приводящие все мнения к единому знаменателю.\n"
    const val HowToStart = "С чего начать?"
    const val HowToStartDelphiMethod = "1) *Предварительный этап*\n" +
            "На этом этапе происходит _отбор экспертной группы_. Он может состоять из разного количества людей, но лучше всего, если их количество не будет превышать 20.\n"
    const val WhatIsNextStep = "Что делать дальше?"
    const val WhatIsNextStepDelphiMethod = "2) *Основной этап*\n" +
            "_Постановка проблемы._ Экспертам предоставляют на рассмотрение главный вопрос, а их задача заключается в том, чтобы разбить его на несколько более мелких. Аналитики отбирают наиболее распространенные вопросы и составляют _общий опросник._\n" +
            "\n" +
            "Сформированный опросник снова представляется на рассмотрение экспертам. Им следует подумать, что можно добавить в него или, как можно еще дополнить информацию в отношении проблемы. В результате этого получается 20 ответов (в том случае если было 20 экспертов) с подробной информацией. \n" +
            "Затем аналитики составляют еще _один опросник_. Новый опросник заново предоставляется на рассмотрение экспертам. На этом этапе они должны предоставить свои _способы решения_ поставленной перед ними задачи и проанализировать мнение своих коллег. В случае если чьи-то мнения расходятся с мнением большинства, – они должны быть озвучены. В результате этого, эксперты вполне могут изменить свое мнение, после чего данный этап повторяется вновь.\n" +
            "\n" +
            "Такие шаги повторяются до тех пор, пока экспертная группа не придет к какому-то _единому мнению_. В это время аналитики тщательно следят за размышлениями экспертов, и при необходимости могут указывать на какие-либо ошибки или недочеты с их стороны. В самом конце подводится итог и составляются *практические рекомендации* по решению поставленной задачи.\n"
    const val WhatIsTheLastStep = "Чем завершается метод Дельфи?"
    const val WhatIsTheLastStepDelphiMethod = "3) *Аналитический этап*\n" +
            "На третьем этапе проверяется согласованность мнений каждого отдельно взятого эксперта. Анализируются полученные результаты и разрабатываются окончательные рекомендации."
    const val DelphiMethodPath = IdeaGenerationMethodsPath + "DelphiMethod.png"

    const val BrainstormMethod = "Метод брейншторм"
    const val BrainstormMethodDescription =
        "*Brainstorm (брейншторм, брейншторминг)* – это метод мозгового штурма для решения конкретной задачи, где участники высказывают свои любые идеи, вплоть до самых сумасшедших и утопических. После этого все идеи анализируются и лучшие могут быть использованы на практике.\n"
    const val SixRules = "6 важных правил проведения мозгового штурма"
    const val SixRulesOfBrainstorm =
        "1. *Подготовьтесь.* Заранее сообщите всем участникам время и место сбора, а также тайминг мероприятия, чтобы все могли выделить нужное время. Позаботьтесь о том, чтобы место проведения было комфортным.  \n" +
                "\n" +
                "2. *Не рассказывайте тему встречи заранее.* Суть мозгового штурма в рождении спонтанных предложений в процессе самого обсуждения.\n" +
                "\n" +
                "3. *Четко сформулируйте задачу в начале встречи.* Например, если встреча посвящена новому мероприятию, но определите конкретно те аспекты, которые будут обсуждаться, например, название мероприятия, основная концепция, стиль.\n" +
                "\n" +
                "4. *Все записывайте.* Назначьте секретаря. Этот человек должен записывать идеи на флипчарте или в блокнот – главное ничего не упустить.\n" +
                "\n" +
                "5. *Не критикуйте и не позволяйте критиковать другим в процессе обсуждения.* Договоритесь о правилах заранее, никто не перебивает, не критикует друг друга и любые самые космические идеи достойны внимания.\n" +
                "\n" +
                "6. *Не собирайте больше 10 человек за раз.* Работа в больших группах плохо управляема и не так эффективна, как в компактных. Если в нашей команде много народу, не бойтесь приглашать людей из разных направлений деятельности, а не только тех, кто занимается темой, которая взята для мозгового штурма.\n" +
                "\n" +
                "_И не забывайте, что дружелюбие, уважение и корректность - лучшие помощники хорошего мозгового штурма._\n"
    const val TechniquesOfCreativityStimulation = "Какие есть техники стимулирования креативности?"
    const val TechniquesOfCreativityStimulationBrainstorm = "1. *Их поменяли мозгами.*\n" +
            "Предложите участникам мозгового штурма записать свои идеи на бумаге. После этого поменяйтесь исписанными листами и попробуйте развить описанные идеи, дать больше вариантов в этом направлении. Отличный способ _сломать_ устоявшийся внутренний _порядок мышления_.\n" +
            "\n" +
            "2. *Техника Stepladder*\n" +
            "Эта техника, на мой взгляд, является наиболее безобидной из всех возможных. Перед дискуссией расскажите всем участникам о проблеме, которую предстоит обсуждать. Дайте им некоторое время для, того чтобы они могли обдумать и сформулировать собственное решение, а затем расформируйте их в группы по два человека. Они должны начать обсуждение между собой. Постепенно увеличивайте количество участников группы, соединяя их. Окончательное решение принимается, когда все участники смогли по очереди высказать свои идеи и выслушать предложения других. \n" +
            "\n" +
            "3. *Созвездие*\n" +
            "Очень интересный подход, опирающийся на _поиск вопросов_, а не ответов. В течение совещания команда должна придумать как можно больше вопросов, касающихся темы обсуждения. Вопросы могут быть любыми: что, где, когда и почему. Этот стиль мозгового штурма помогает рассмотреть проект во всех возможных его аспектах. Отличная техника для команд, которые фокусируются на решении нескольких проблем в проекте и уже в последние минуты пытаются закрыть возникшие вопросы.\n"
    const val HowToFindAGeniusIdea = "Как выделить гениальную идею?"
    const val HowToFindAGeniusIdeaBrainstorm =
        "Логическим завершением мозгового штурма является принятие решения, выбор той самой идеи, которая будет воплощена в жизнь.\n" +
                "\n" +
                "Если Ваш секретарь не забыл все записать, то первым делом вычеркиваем:\n" +
                "1. То, на что *нет ресурсов*, в том числе не хватит времени\n" +
                "2. То, что *никому не интересно* и не поддерживается командой\n" +
                "3. То, что в корне *противоречит* вашей **концепции**\n" +
                "\n" +
                "_Дальше останется ТОП мыслей, из которых уже и будет выбрана та самая идеальная и гениальная идея, которую вы будете развивать и реализовывать._\n"
    const val BrainstormMethodPath = IdeaGenerationMethodsPath + "BrainstormMethod.png"

    const val Scamper = "SCAMPER"
    const val ScamperDescription =
        "*«SCAMPER»* — это схема постановки определённых вопросов, которые стимулируют генерацию новых идей:\n" +
                "*S* — Substitute (замещение)\n" +
                "*C* — Combine (комбинирование)\n" +
                "*A* — Adapt (адаптация)\n" +
                "*M* — Modify/Magnify (модификация, увеличение)\n" +
                "*P* — Put to Other Uses (предложение другого применения)\n" +
                "*E* — Eliminate (устранение или сведение действия до минимума)\n" +
                "*R* — Rearrange/Reverse (обращение, изменение порядка)"
    const val HowDoesScamperWork =
        "Перед применением методики необходимо чётко поставить задачу: определить проблему, которая требует решения, или идею, которая должна быть разработана. Причём, использоваться методика может абсолютно в любой сфере: личной жизни, семейных отношениях, работе, бизнесе, предоставлении товаров и услуг и т.д. Как только задача поставлена, задаются вопросы по схеме «SCAMPER», которая включает в себя более 60 вопросов для генерации идей и примерно 200 слов, способствующих возникновению ассоциаций. Итак, попробуем!"
    const val ScamperPath = IdeaGenerationMethodsPath + "SCAMPER.png"

    const val TrendyFriendy = "Trendy Friendy"
    const val BackToSteps = "Обратно к шагам"
    const val BackToIdeaGeneration = "Попробовать другие техники"
    const val HowDoesItWork = "Как это работает?"
    const val GetIllustration = "Получить картинку, иллюстрирующую метод"

    const val TrendyFriendyDescription =
        "Это игра по генерации идей на основе анализа и комбинации трендов - собственная разработка автора, сочетающая в себе подходы Babson college of Entrepreneurship по генерации ассоциаций в формате Idea Lab, Ideation от Cambridge Judge Business School, а также собственный опыт автора в рамках сотрудничества с Высшей школой технологического предпринимательства СПбПУ Петра Великого. Комбинация вышеперечисленных подходов была заложена в основу игры \n"
    const val HowDoesTrendyFriendyWork =
        "Игра основывается на базе знаний - наборе разных трендов из актуальных источников, занимающихся анализом трендов и их формированием на ежегодной основе. База знаний дает всегда самую свежую информацию по текущей ситуации в области трендов.  Участники игры должны за отведенное время в течение нескольких раундов на основе изученных трендов сгенерить идеи.Количество раундов не ограничено, завершается игра путем генерации итогового пула идей для дальнейшего анализа и формирования итоговой идеи продукта для перехода на следующий шаг."
    const val TrendyFriendyPath = IdeaGenerationMethodsPath + "TrendyFriendy.jpg"
    const val TrendyFriendyStart = "Нажми на кнопку, чтобы начать игру"
    const val TrendyFriendyOpen = "Start"
    const val IdeasSpreadsheetName = "Идеация"
    const val IdeasSpreadsheetNumber = "№ п/п"
    const val IdeasSpreadsheetDescription = "Описание идеи"
    const val IdeasSpreadsheetTechnical =
        "Идея технически реализуема (можно найти ресурсы для ее реализации в реальной жизни)"
    const val IdeasSpreadsheetEconomical = "Идея экономически реализуема (можно найти потребителя и оценить экономику)"

    val IdeaGenerationWithDescription = mapOf(
        Bisociation to IdeaGenerationInfo(
            mapOf(
                Pair(BisociationDescription, HowDoesItWork),
                Pair(HowDoesBisociationWork, HeadingOfSystematization),
                Pair(HeadingOfSystematizationInBisociation, GetIllustration)
            ),
            BisociationPath
        ),
        DelphiMethod to IdeaGenerationInfo(
            mapOf(
                Pair(DelphiMethodDescription, HowDoesItWork),
                Pair(HowDoesDelphiMethodWork, GroupsOfPeople),
                Pair(GroupsOfPeopleDelphiMethod, HowToStart),
                Pair(HowToStartDelphiMethod, WhatIsNextStep),
                Pair(WhatIsNextStepDelphiMethod, WhatIsTheLastStep),
                Pair(WhatIsTheLastStepDelphiMethod, GetIllustration)
            ), DelphiMethodPath
        ),
        BrainstormMethod to IdeaGenerationInfo(
            mapOf(
                Pair(BrainstormMethodDescription, SixRules),
                Pair(SixRulesOfBrainstorm, TechniquesOfCreativityStimulation),
                Pair(TechniquesOfCreativityStimulationBrainstorm, HowToFindAGeniusIdea),
                Pair(HowToFindAGeniusIdeaBrainstorm, GetIllustration)
            ),
            BrainstormMethodPath
        ),
        Scamper to IdeaGenerationInfo(
            mapOf(
                Pair(ScamperDescription, HowDoesItWork),
                Pair(HowDoesScamperWork, GetIllustration)
            ), ScamperPath
        ),
        TrendyFriendy to IdeaGenerationInfo(
            mapOf(
                Pair(TrendyFriendyDescription, HowDoesItWork),
                Pair(HowDoesTrendyFriendyWork, GetIllustration)
            ), TrendyFriendyPath
        )
    )
}
