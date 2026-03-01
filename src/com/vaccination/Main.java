package com.vaccination;

import com.vaccination.ui.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 *  VacciTrack – Vaccination Drive Management System
 *  Muffakham Jah College of Engineering and Technology
 *  Department of Computer Science and Artificial Intelligence
 *  MCA Industry Ready Program (IA-407) | AY 2025–26 IV Semester
 *  Java Programming Module – Week 5 Final Project
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
