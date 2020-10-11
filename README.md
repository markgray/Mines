Mines - An Android minesweeper game
===================================

This is a version of the classic minesweeper game for the Android platform using kotlin,
JetPack Navigation, ViewModel and Room. As far as possible efforts will be made to keep it
up to date with recommended best practices 
 
 Structure
 ---------
 
 There are four fragments used: ChooseFragment, CustomSizeDialog, GameFragment, and ScoreFragment.
 
  - ChooseFragment allows you to choose the size of the game board and is the "app:startDestination"
  of the navigation graph. From there you can navigate to GameFragment to begin playing, or to
  CustomSizeDialog to configure a custom size for the game board.
  
  - CustomSizeDialog allows you to configure a custom size for the game board, and once you have
  done so you can navigate to the GameFragment, or return to the ChooseFragment by pressing the
  back button.
  
  - GameFragment is where the actual game is played. From there you will navigate to ScoreFragment
  once you have correctly marked all of the mine locations, or you can use the back button to return
  to ChooseFragment to play a new game.
  
  - ScoreFragment displays the "score" of the last game as well containing a RecyclerView which
  displays the score of all previous games. From there you will navigate to ChooseFragment to play
  a new game, or if you click one of the items displayed in the RecyclerView you can navigate back
  to GameFragment to replay that particular game.