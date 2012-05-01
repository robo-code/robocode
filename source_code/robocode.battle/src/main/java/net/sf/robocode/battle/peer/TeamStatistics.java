// /*******************************************************************************
// * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Common Public License v1.0
// * which accompanies this distribution, and is available at
// * http://robocode.sourceforge.net/license/cpl-v10.html
// *
// * Contributors:
// *     Mathew A. Nelson
// *     - Initial API and implementation
// *     Luis Crespo
// *     - Added getCurrentScore()
// *     Flemming N. Larsen
// *     - Ported to Java 5
// *     - Renamed method names and removed unused methods
// *     - Ordered all methods more naturally
// *     - Added methods for getting current scores
// *******************************************************************************/
// package net.sf.robocode.battle.peer;
//
//
// import robocode.BattleResults;
//
//
// /**
// * @author Mathew A. Nelson (original)
// * @author Luis Crespo (contributor)
// * @author Flemming N. Larsen (contributor)
// */
// public class TeamStatistics extends ContestantStatistics {
//
// private final TeamPeer teamPeer;
// private int rank;
//
// public TeamStatistics(TeamPeer teamPeer) {
// this.teamPeer = teamPeer;
// }
//
// public void setRank(int rank) {
// this.rank = rank;
// }
//
// public double getTotalScore() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getCombinedScore();
// }
// return d;
// }
//
// public double getTotalSurvivalScore() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getTotalSurvivalScore();
// }
// return d;
// }
//
// public double getTotalLastSurvivorBonus() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getTotalLastSurvivorBonus();
// }
// return d;
// }
//
// public double getTotalBulletDamageScore() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getTotalBulletDamageScore();
// }
// return d;
// }
//
// public double getTotalBulletKillBonus() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getTotalBulletKillBonus();
// }
// return d;
// }
//
// public double getTotalRammingDamageScore() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getTotalRammingDamageScore();
// }
// return d;
// }
//
// public double getTotalRammingKillBonus() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getTotalRammingKillBonus();
// }
// return d;
// }
//
// public int getTotalFirsts() {
// int d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getTotalFirsts();
// }
// return d;
// }
//
// public int getTotalSeconds() {
// int d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getTotalSeconds();
// }
// return d;
// }
//
// public int getTotalThirds() {
// int d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getTotalThirds();
// }
// return d;
// }
//
// public double getCurrentScore() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getCurrentScore();
// }
// return d;
// }
//
// public double getCurrentSurvivalScore() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getCurrentSurvivalScore();
// }
// return d;
// }
//
// public double getCurrentSurvivalBonus() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getCurrentSurvivalBonus();
// }
// return d;
// }
//
// public double getCurrentBulletDamageScore() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getCurrentBulletDamageScore();
// }
// return d;
// }
//
// public double getCurrentBulletKillBonus() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getCurrentBulletKillBonus();
// }
// return d;
// }
//
// public double getCurrentRammingDamageScore() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getCurrentRammingDamageScore();
// }
// return d;
// }
//
// public double getCurrentRammingKillBonus() {
// double d = 0;
//
// for (RobotPeer teammate : teamPeer) {
// d += teammate.getRobotStatistics().getCurrentRammingKillBonus();
// }
// return d;
// }
//
// public BattleResults getFinalResults() {
// return new BattleResults(teamPeer.getName(), rank, getTotalScore(), getTotalSurvivalScore(),
// getTotalLastSurvivorBonus(), getTotalBulletDamageScore(), getTotalBulletKillBonus(),
// getTotalRammingDamageScore(), getTotalRammingKillBonus(), getTotalFirsts(), getTotalSeconds(),
// getTotalThirds());
// }
//
// public IContestantStatistics fakeConstructor(RobotPeer peer, int robots) {
// // TODO Auto-generated method stub
// return null;
// }
//
// public void scoreBulletDamage(int robot, double damage) {
// // TODO Auto-generated method stub
//
// }
//
// public double scoreBulletKill(int robot) {
// // TODO Auto-generated method stub
// return 0;
// }
//
// public void scoreFirsts() {
// // TODO Auto-generated method stub
//
// }
//
// public void scoreLastSurvivor() {
// // TODO Auto-generated method stub
//
// }
//
// public void scoreRammingDamage(int robot) {
// // TODO Auto-generated method stub
//
// }
//
// public double scoreRammingKill(int robot) {
// // TODO Auto-generated method stub
// return 0;
// }
//
// public void scoreRobotDeath(int enemiesRemaining) {
// // TODO Auto-generated method stub
//
// }
//
// public void scoreSurvival() {
// // TODO Auto-generated method stub
//
// }
// }
