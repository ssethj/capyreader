package com.capyreader.app

/**
 * Bridges hardware volume key events received by [MainActivity] to the
 * article reader currently on screen.
 *
 * Each reader registers its navigation callbacks under a unique owner token
 * for the lifetime of its composition and removes exactly its own entry on
 * disposal, so overlapping compositions can't clear each other's
 * registrations. When no reader is registered (or volume key navigation is
 * disabled), both callbacks are null and the keys fall through to the
 * system's default volume behavior.
 *
 * Key events and composition both run on the main thread, so no additional
 * synchronization is needed.
 */
class VolumeKeyNavigationBridge internal constructor() {
    private val registrations = linkedMapOf<Any, Callbacks>()

    fun register(owner: Any, callbacks: Callbacks) {
        registrations[owner] = callbacks
    }

    fun unregister(owner: Any) {
        registrations.remove(owner)
    }

    private val latestRegistration: Callbacks?
        get() = registrations.values.lastOrNull()

    val onSelectPreviousArticle: (() -> Unit)?
        get() = latestRegistration?.onSelectPreviousArticle

    val onSelectNextArticle: (() -> Unit)?
        get() = latestRegistration?.onSelectNextArticle

    data class Callbacks(
        val onSelectPreviousArticle: () -> Unit,
        val onSelectNextArticle: () -> Unit,
    )
}
