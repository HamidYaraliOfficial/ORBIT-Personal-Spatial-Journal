package com.orbit.spatialjournal.di

import com.orbit.spatialjournal.ai.AIAssistantProvider
import com.orbit.spatialjournal.ai.LocalRuleBasedAssistant
import com.orbit.spatialjournal.voice.AndroidSpeechRecognizerImpl
import com.orbit.spatialjournal.voice.SpeechToTextProvider
import com.orbit.spatialjournal.map.GoogleMapProviderImpl
import com.orbit.spatialjournal.map.MapProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {
    @Binds @Singleton abstract fun bindAiAssistant(impl: LocalRuleBasedAssistant): AIAssistantProvider
    @Binds @Singleton abstract fun bindSpeechToText(impl: AndroidSpeechRecognizerImpl): SpeechToTextProvider
    @Binds @Singleton abstract fun bindMapProvider(impl: GoogleMapProviderImpl): MapProvider
}
