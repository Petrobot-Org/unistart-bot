package ru.spbstu.application.telegram

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.NullableRequestBuilder
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitContentMessage
import dev.inmo.tgbotapi.requests.abstracts.Request
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.content.ContactContent
import dev.inmo.tgbotapi.types.message.content.DocumentContent
import dev.inmo.tgbotapi.types.message.content.MessageContent
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.RiskFeature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@OptIn(RiskFeature::class)
private suspend inline fun <reified O : MessageContent> BehaviourContext.waitFrom(
    chat: Chat,
    initRequest: Request<*>? = null,
    includeMediaGroups: Boolean = true,
    noinline errorFactory: NullableRequestBuilder<*> = { null }
): Flow<O> = waitContentMessage<O>(initRequest, includeMediaGroups, errorFactory)
    .filter { it.chat.id == chat.id }
    .map { it.content }

suspend fun BehaviourContext.waitContactFrom(
    chat: Chat,
    initRequest: Request<*>? = null,
    errorFactory: NullableRequestBuilder<*> = { null }
) = waitFrom<ContactContent>(chat, initRequest, false, errorFactory)

suspend fun BehaviourContext.waitTextFrom(
    chat: Chat,
    initRequest: Request<*>? = null,
    errorFactory: NullableRequestBuilder<*> = { null }
) = waitFrom<TextContent>(chat, initRequest, false, errorFactory)

suspend fun BehaviourContext.waitDocumentFrom(
    chat: Chat,
    initRequest: Request<*>? = null,
    errorFactory: NullableRequestBuilder<*> = { null },
    includeMediaGroups: Boolean = false
) = waitFrom<DocumentContent>(chat, initRequest, includeMediaGroups, errorFactory)
