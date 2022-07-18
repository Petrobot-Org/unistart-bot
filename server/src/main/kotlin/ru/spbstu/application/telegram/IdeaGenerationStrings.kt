package ru.spbstu.application.telegram

import ru.spbstu.application.steps.entities.IdeaGenerationInfo

object IdeaGenerationStrings {
    const val IdeaGenerationMethodsPath = "/static/ideaGeneration/"
    const val ChooseIdeaGeneration = "Выбери технику генерации идей"
    const val Bisociation = "Бисоциации"
    const val BisociationDescription =
        "Бисоциация – это комбинация из двух идей, взятых из разных контекстов, ранее казавшихся несовместимыми." +
                "Эффективность связанной с этим понятием креативной техники в том, что человек вынужден уйти от привычных схем мышления и устанавливать связи между, казалось бы, не связанными друг с другом понятиями."
    const val HowDoesBisociationWork = "Пример:\n" +
            "Берется название предмета, подлежащего проектированию. Справа пишут 6-7 «слов-ответов», то есть существительных, произвольно взятых из словаря или подсказанных коллегами. Важно, чтобы в уме проектировщика эти слова не были связаны с предметом, который следует спроектировать. Для систематизации найденных решений их располагают под следующими рубриками:\n" +
            "\n" +
            "СЕЙЧАС (продукция или система может быть изготовлена немедленно);\n" +
            "\n" +
            "2-5 ЛЕТ (концепция не совсем готова для производства);\n" +
            "\n" +
            "5-10 ЛЕТ (ответ ведет к долгосрочному планированию продукции или системы);\n" +
            "\n" +
            "Исследования и Разработки (решение представляется разумным, а его осуществимость должен определить отдел исследований и разработок);\n" +
            "\n" +
            "ТРЮКИ (иногда в результате получается идея, которая не имеет ничего общего с самой продукцией, но дает новый рекламный трюк);\n" +
            "\n" +
            "ДРУГОЕ (часто появляются идеи, которые вовсе не являются решениями данной проблемы дизайна. Тем не менее они могут стать новаторскими для решения проблем, находящихся за пределами исследования, и разработаны для других клиентов)."
    const val BisociationPath = IdeaGenerationMethodsPath + "Bisociation.png"
    const val DelphiMethod = "Метод Дельфи"
    const val DelphiMethodDescription =
        "Метод Дельфи – это метод быстрого поиска решений, основанный на их генерации в процессе «мозговой атаки», проводимой группой специалистов, и отбора лучшего решения, исходя из экспертных оценок."
    const val HowDoesDelphiMethodWork =
        "Метод Дельфи позволяет проводить экспертное оценивание, главными его особенностями является анонимность, заочность и многоуровневость. при помощи определенных приемов, таких как мозговые штурмы, опросы или интервью, удается отыскать способ для определения правильного решения. То есть группа независимых друг от друга экспертов может гораздо лучше дать оценку той или иной ситуации, нежели структурированная группа людей. Поскольку участники могут даже не знать о существовании друг друга, – это полностью исключает столкновение их интересов и мнений.\n" +
                "В ходе использования данного метода принимают участие две группы людей:\n" +
                "1-я группа – это эксперты, имеющие свою точку зрения по какому-то вопросу.\n" +
                "2-я группа – это аналитики, приводящие все мнения к единому знаменателю.\n" +
                "Метод Дельфи состоит из нескольких этапов:\n" +
                "1) Предварительный этап\n" +
                "На этом этапе происходит отбор экспертной группы. Он может состоять из разного количества людей, но лучше всего, если их количество не будет превышать 20.\n" +
                "2) Основной этап\n" +
                "Постановка проблемы. Экспертам предоставляют на рассмотрение главный вопрос, а их задача заключается в том, чтобы разбить его на несколько более мелких. Аналитики отбирают наиболее распространенные вопросы и составляют общий опросник.\n" +
                "Сформированный опросник снова представляется на рассмотрение экспертам. Им следует подумать, что можно добавить в него или, как можно еще дополнить информацию в отношении проблемы. В результате этого получается 20 ответов (в том случае если было 20 экспертов) с подробной информацией. Затем аналитики составляют еще один опросник.\n" +
                "Новый опросник заново предоставляется на рассмотрение экспертам. На этом этапе они должны предоставить свои способы решения поставленной перед ними задачи и проанализировать мнение своих коллег. В случае если чьи-то мнения расходятся с мнением большинства, – они должны быть озвучены. В результате этого, эксперты вполне могут изменить свое мнение, после чего данный этап повторяется вновь.\n" +
                "Такие шаги повторяются до тех пор, пока экспертная группа не придет к какому-то единому мнению. В это время аналитики тщательно следят за размышлениями экспертов, и при необходимости могут указывать на какие-либо ошибки или недочеты с их стороны. В самом конце подводится итог и составляются практические рекомендации по решению поставленной задачи.\n" +
                "3) Аналитический этап\n" +
                "На третьем этапе проверяется согласованность мнений каждого отдельно взятого эксперта. Анализируются полученные результаты и разрабатываются окончательные рекомендации."

    const val DelphiMethodPath = IdeaGenerationMethodsPath + "DelphiMethod.png"
    const val BrainstormMethod = "Метод брейншторм"
    const val BrainstormMethodDescription =
        "Brainstorm (брейншторм, брейншторминг)  – это метод мозгового штурма для решения конкретной задачи, где участники высказывают свои любые идеи, вплоть до самых сумасшедших и утопических. После этого все идеи анализируются и лучшие могут быть использованы на практике. \n"
    const val HowDoesBrainstormMethodWork = "6 важных правил проведения мозгового штурма\n" +
            "1. Подготовьтесь. Заранее сообщите всем участникам время и место сбора, а также тайминг мероприятия, чтобы все могли выделить нужное время. Позаботьтесь о том, чтобы место проведения было комфортным. Внимание!\n" +
            "2. Не рассказывайте тему встречи заранее. Суть мозгового штурма в рождении спонтанных предложений в процессе самого обсуждения.\n" +
            "3. Четко сформулируйте задачу в начале встречи. Например, если встреча посвящена новому мероприятию, но определите конкретно те аспекты, которые будут обсуждаться, например, название мероприятия, основная концепция, стиль.\n" +
            "4. Все записывайте. Назначьте секретаря. Этот человек должен записывать идеи на флипчарте или в блокнот – главное ничего не упустить.\n" +
            "5. Не критикуйте и не позволяйте критиковать другим в процессе обсуждения. Договоритесь о правилах заранее, никто не перебивает, не критикует друг друга и любые самые космические идеи достойны внимания.\n" +
            "6. Не собирайте больше 10 человек за раз. Работа в больших группах плохо управляема и не так эффективна, как в компактных. Если в нашей команде много народу, не бойтесь приглашать людей из разных направлений деятельности, а не только тех, кто занимается темой, которая взята для мозгового штурма.\n" +
            "И не забывайте, что дружелюбие, уважение и корректность - лучшие помощники хорошего мозгового штурма.\n" +
            "Техники, кторые вам помогут для стимулирования креативности:\n" +
            "1. Их поменяли мозгами.\n" +
            "Предложите участникам мозгового штурма записать свои идеи на бумаге. После этого поменяйтесь исписанными листами и попробуйте развить описанные идеи, дать больше вариантов в этом направлении. Отличный способ сломать устоявшийся внутренний порядок мышления.\n" +
            "2. Техника Stepladder\n" +
            "Эта техника, на мой взгляд, явялестя наиболее безобидной из всех возможных. Перед дискуссией расскажите всем участникам о проблеме, которую предстоит обсуждать. Дайте им некоторое время для, того чтобы они могли обдумать и сформулировать собственное решение, а затем расформируйте их в группы по два человека. Они должны начать обсуждение между собой. Постепенно увеличивайте количество участников группы, соединяя их. Окончательное решение принимается, когда все участники смогли по очереди высказать свои идеи и выслушать предложения других. \n" +
            "3. Созвездие\n" +
            "Очень интересный подход, опирающийся на поиск вопросов, а не ответов. В течение совещания команда должна придумать как можно больше вопросов, касающихся темы обсуждения. Вопросы могут быть любыми: что, где, когда и почему. Этот стиль мозгового штурма помогает рассмотреть проект во всех возможных его аспектах. Отличная техника для команд, которые фокусируются на решении нескольких проблем в проекте и уже в последние минуты пытаются закрыть возникшие вопросы.\n" +
            "Логическим завершением мозгового штурма является принятие решения, выбор той самой идеи, которая будет воплощена в жизнь.\n" +
            "Если Ваш секретарь не забыл все записать, то первым делом вычеркиваем:\n" +
            "     1. То, на что нет ресурсов, в том числе не хватит времени\n" +
            "     2. То, что никому не интересно и не поддерживается командой\n" +
            "     3. То, что в корне противоречит вашей концепции\n" +
            "Дальше останется ТОП мыслей, из которых уже и будет выбрана та самая идеальная и гениальная идея, которую вы будете развивать и реализовывать."
    const val BrainstormMethodPath = IdeaGenerationMethodsPath + "BrainstormMethod.png"
    const val Scamper = "SCAMPER"
    const val ScamperDescription =
        "«SCAMPER» — это схема постановки определённых вопросов, которые стимулируют генерацию новых идей:\n" +
                "S — Substitute (замещение)\n" +
                "C — Combine (комбинирование)\n" +
                "A — Adapt (адаптация)\n" +
                "M — Modify/Magnify (модификация, увеличение)\n" +
                "P — Put to Other Uses (предложение другого применения)\n" +
                "E — Eliminate (устранение или сведение действия до минимума)\n" +
                "R — Rearrange/Reverse (обращение, изменение порядка)"
    const val HowDoesScamperWork =
        "Перед применением методики необходимо чётко поставить задачу: определить проблему, которая требует решения, или идею, которая должна быть разработана. Причём, использоваться методика может абсолютно в любой сфере: личной жизни, семейных отношениях, работе, бизнесе, предоставлении товаров и услуг и т.д. Как только задача поставлена, задаются вопросы по схеме «SCAMPER», которая включает в себя более 60 вопросов для генерации идей и примерно 200 слов, способствующих возникновению ассоциаций. Итак, попробуем!\n" +
                "SUBSTITUTE (ЗАМЕЩЕНИЕ)\n" +
                "Нужно поразмыслить о том, как и чем можно заменить часть имеющейся проблемы, сервиса или процесса; людей, схемы действий, эмоции и т.д., что уже само по себе нередко приводит к появлению новых идей.\n" +
                "Вопросы:\n" +
                "Как и чем можно заменить составляющие части?\n" +
                "Как и чем можно заменить имеющиеся правила?\n" +
                "Как и чем можно заменить форму?\n" +
                "Как и чем можно заменить запах, звук, поверхность, цвет?\n" +
                "Как и кем можно заменить участников процесса?\n" +
                "Можно ли изменить название?\n" +
                "Можно ли заменить одну часть другой?\n" +
                "Можно ли применить данную идею в новом направлении?\n" +
                "Можно ли изменить свои ощущения, связанные с этим?\n" +
                "Можно ли изменить своё отношение к этому?\n" +
                "Слова для ассоциаций: переключить, поменять местами, занять место, заменитель, подменить, придать форму, отложить, сменить место, заменить, сменить имя, освободить, заместитель, замена, обменять, изменить.\n" +
                "COMBINE (КОМБИНИРОВАНИЕ)\n" +
                "Попытайтесь понять, можно ли скомбинировать несколько частей поставленной проблемы для её решения или усиления взаимодействия этих частей. Креативное мышление и генерация идей предполагают именно сочетание уже имеющихся, но изначально не связанных друг с другом частей проблемы или замысла для создания того, чего ещё нет.\n" +
                "Вопросы:\n" +
                "Можно ли скомбинировать несколько замыслов или их частей и как?\n" +
                "Можно ли скомбинировать результаты каждого замысла или их частей и как?\n" +
                "Можно ли скомбинировать поставленную задачу с чем-то другим?\n" +
                "Что может быть скомбинировано, чтобы расширилась область применения?\n" +
                "Какие части (материалы, товары, услуги) могут быть скомбинированы?\n" +
                "Можно ли скомбинировать различные способности для достижения результата?\n" +
                "Слова для ассоциаций: объединить, соотнести, скомплектовать, связать, перемешать, соединить, собрать воедино, создать союз, совместить, смешать.\n" +
                "ADAPT (АДАПТАЦИЯ)\n" +
                "Задумайтесь о том, как с помощью уже имеющихся идей и способов решить новые задачи? Вполне вероятно, что это и будет нужным решением. Здесь важно руководствоваться тем, что все новые идеи состоят из частей уже существующих.\n" +
                "Вопросы:\n" +
                "Существуют ли аналоги, и на что это может быть похоже?\n" +
                "Сталкивался ли я уже с чем-то подобным?\n" +
                "Что ещё можно извлечь из этой ситуации?\n" +
                "Чем из уже существующего я могу воспользоваться, чтобы решить эту проблему?\n" +
                "Могу ли я скопировать кого-то и чьи идеи я могу использовать для себя?\n" +
                "Какие уже существующие идеи я могу адаптировать под себя?\n" +
                "Может ли моя концепция быть представлена в другим контексте?\n" +
                "Есть ли в других областях идеи, которые можно было бы применить?\n" +
                "Слова для ассоциаций: варьировать, трансформировать, устанавливать, перерабатывать, оценивать, проверять, переигрывать, модернизировать, соотносить, соответствовать, искать свой стиль, применять, заимствовать, копировать, связывать, подстраиваться, приспосабливаться.\n" +
                "MODIFY/MAGNIFY (МОДИФИКАЦИЯ, УВЕЛИЧЕНИЕ)\n" +
                "Поищите возможные способы расширения или модификации имеющихся идей. Таким образом можно добиться не только трансформации существующих вариантов решения проблемы, но и суметь увидеть её под новым ракурсом, а также увеличить эффективность своих действий.\n" +
                "Вопросы:\n" +
                "Что и как можно модифицировать?\n" +
                "Какие идеи можно расширить и как?\n" +
                "Как и что можно сделать с большей эффективностью?\n" +
                "Можно ли это легко повторить?\n" +
                "Можно ли придать имеющимся идеям и концепциям дополнительную ценность?\n" +
                "Слова для ассоциаций: сделать эффективнее, увеличить, акцентировать, придать значимость, сделать интенсивнее, повысить, распространить, усилить, добавить, применить.\n" +
                "PUT TO OTHER USES (ПРЕДЛОЖЕНИЕ ДРУГОГО ПРИМЕНЕНИЯ)\n" +
                "Есть ли у имеющейся на данный момент идеи другие области применения? Есть ли среди того, что вы использовали ранее, что-то, при помощи чего вы могли бы решить настоящую проблему? Во многих случаях всего лишь одним способом можно решить несколько задач. Нужно лишь увидеть эту возможность.\n" +
                "Вопросы:\n" +
                "Как ещё это можно использовать?\n" +
                "Может ли это быть применено не только к тому, что планировалось, но и к чему-либо другому?\n" +
                "Как данную идею мог бы использовать ребёнок или пожилой человек?\n" +
                "Можно ли применить имеющуюся идею, модифицировав её?\n" +
                "Как бы я оценил эту идею, если бы узнал её только сейчас?\n" +
                "Слова для ассоциаций: обрабатывать, оперировать, использовать преимущества, манипулировать, извлекать пользу, разбираться, расширять, задействовать, использовать, применять, делать доступным, переставлять, делать более удобным.\n" +
                "ELIMINATE (УСТРАНЕНИЕ ИЛИ СВЕДЕНИЕ ДО МИНИМУМА)\n" +
                "Представьте, что случится, если некоторые из частей вашей нынешней идеи или концепции будут устранены или минимизированы с целью её дальнейшей разработки? Попробуйте избавиться от нескольких составляющих, варьируйте компоненты, переставляйте слагаемые местами – это поможет вам сузить круг проблем и перейти от общего к частному.\n" +
                "Вопросы:\n" +
                "Можно ли упростить эту проблему?\n" +
                "Что можно удалить из контекста без существенных изменений?\n" +
                "Какое условие не является обязательным?\n" +
                "Можно ли сделать исключение из правил?\n" +
                "Можно ли и нужно ли разделять проблему на несколько частей?\n" +
                "Можно ли придать этому меньший объём?\n" +
                "Слова для ассоциаций: стереть, сократить, упростить, убрать, очистить, смоделировать, ликвидировать, ограничить, подавить, избавиться, удалить, исключить, искоренить, игнорировать, контролировать, отменить, упразднить.\n" +
                "REARRANGE/REVERSE (ОБРАЩЕНИЕ, ИЗМЕНЕНИЕ ПОРЯДКА)\n" +
                "Задумайтесь над тем, есть ли возможность действовать в обратном порядке, изменить последовательность действий, что для этого нужно сделать и как это будет выглядеть? В некоторых ситуациях это позволяет быстро найти решение проблемы и способствует появлению новых идей.\n" +
                "Вопросы:\n" +
                "Какой порядок будет наиболее оптимальным?\n" +
                "Являются ли отдельные части взаимозаменяемыми?\n" +
                "Может ли быть иная последовательность действий?\n" +
                "Можно ли поменять местами причину и следствие?\n" +
                "Можно ли поменять местами положительные и отрицательные аспекты?\n" +
                "Что будет, если рассмотреть проблему в обратном порядке?\n" +
                "Что будет, если я буду действовать от обратного?\n" +
                "Слова для ассоциаций: удалить, переделать, повернуть, перевернуть, заменить, поменять, переставить, реорганизовать, изменить концепцию, поменять порядок, инвертировать, вернуть, прервать, переместить, изменить.\n" +
                "Как можно заметить, методика «SCAMPER» довольно проста в применении, хотя на первый взгляд может показаться иначе. Важно понять, что во многом она подразумевает разрыв шаблона и поиск новых способов решения проблем и генерации идей, основывающийся на уже существующих решениях и идеях. Но в гораздо большей степени здесь важна именно практика, т.к. без неё методика не принесёт никакого результата. Следуйте технике «SCAMPER» и новые идеи, а также интересные способы решения проблем не заставят себя долго ждать."
    const val ScamperPath = IdeaGenerationMethodsPath + "SCAMPER.png"
    const val TrendyFriendy = "Trendy Friendy"
    const val BackToSteps = "Обратно к шагам"
    const val BackToIdeaGeneration = "Попробовать другие техники"
    const val HowDoesItWork = "Как это работает?"

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
        Bisociation to IdeaGenerationInfo(BisociationDescription, HowDoesBisociationWork, BisociationPath),
        DelphiMethod to IdeaGenerationInfo(DelphiMethodDescription, HowDoesDelphiMethodWork, DelphiMethodPath),
        BrainstormMethod to IdeaGenerationInfo(
            BrainstormMethodDescription,
            HowDoesBrainstormMethodWork,
            BrainstormMethodPath
        ),
        Scamper to IdeaGenerationInfo(ScamperDescription, HowDoesScamperWork, ScamperPath),
        TrendyFriendy to IdeaGenerationInfo(
            TrendyFriendyDescription, HowDoesTrendyFriendyWork, TrendyFriendyPath
        )
    )
}