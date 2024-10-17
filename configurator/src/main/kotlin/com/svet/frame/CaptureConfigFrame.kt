package com.svet.frame

import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Color
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame

private val log = KotlinLogging.logger {}

class CaptureConfigFrame internal constructor() : JFrame("Press ESC to exit") {

    private var panel: CaptureConfigPanel = CaptureConfigPanel()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        this.extendedState = MAXIMIZED_BOTH
        this.isUndecorated = true
        this.add(panel)
//        pack()
        setLocationRelativeTo(null)
        this.isVisible = true
//        this.background = Color.DARK_GRAY
        this.background = Color(0, 0, 0, 0)

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(evt: KeyEvent) {
                if (evt.keyCode == KeyEvent.VK_ESCAPE) {
                    log.info { "close frame" }
                    panel.timer.stop()
                    dispose() // exitProcess(0)
                }
            }
        })

    }

}
