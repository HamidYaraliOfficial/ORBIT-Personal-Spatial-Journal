package com.orbit.spatialjournal.ui.screens.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlin.math.cos
import kotlin.math.sin

/**
 * Personal Graph View: a lightweight circular-layout renderer (no external graph library) —
 * nodes are placed evenly around a circle sized to the node count, edges are drawn as lines
 * between them. Good enough to visualize relationships for a few dozen memories at a time;
 * the Related Memories panel on Memory Detail is the primary way to explore the graph.
 */
@Composable
fun GraphScreen(navController: NavHostController, viewModel: GraphViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Personal Graph") }) }) { padding ->
        Canvas(modifier = Modifier.fillMaxSize().padding(padding)) {
            val nodeCount = state.nodes.size
            if (nodeCount == 0) return@Canvas
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (minOf(size.width, size.height) / 2f) * 0.8f
            val positions = state.nodes.mapIndexed { index, node ->
                val angle = 2 * Math.PI * index / nodeCount
                node.id to Offset(
                    center.x + radius * cos(angle).toFloat(),
                    center.y + radius * sin(angle).toFloat()
                )
            }.toMap()

            state.edges.forEach { edge ->
                val from = positions[edge.fromId] ?: return@forEach
                val to = positions[edge.toId] ?: return@forEach
                drawLine(color = Color.Gray.copy(alpha = 0.4f), start = from, end = to, strokeWidth = 1.5f)
            }
            positions.values.forEach { pos ->
                drawCircle(color = Color(0xFF1F6FEB), radius = 8f, center = pos)
            }
        }
    }
}
