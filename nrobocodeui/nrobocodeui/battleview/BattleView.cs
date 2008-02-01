using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.Drawing.Drawing2D;
using System.Windows.Forms;
using robocode.battle;
using robocode.battlefield;
using robocode.ui;
using nrobocodeui.dialog;
using nrobocodeui.gfx;

namespace nrobocodeui.battleview
{
    public partial class BattleView : UserControl, IBattleView
    {
        private RobocodeFrame robocodeFrame;

        private BattleField battleField;

        private double scale = 1.0;

        // Ground
        private int[,] groundTiles;

        private int groundTileWidth = 64;
        private int groundTileHeight = 64;

        private Bitmap groundImage;

        private bool drawGround = true;

        public BattleView()
        {

            InitializeComponent();
        }

        public void InitFrame(RobocodeFrame robocodeFrame)
        {
            this.robocodeFrame = robocodeFrame;
        }

        #region IBattleView Members

        public void setBattleField(BattleField bf)
        {
            //TODO
        }

        public void setVisible(bool b)
        {
            //TODO
        }

        public void setInitialized(bool b)
        {
            //TODO
        }

        public void setBattle(Battle b)
        {
            //TODO
        }

        public void repaint()
        {
            //TODO
        }

        public void setDisplayOptions()
        {
            //TODO
        }

        public void update()
        {
            //TODO
        }

        public bool isDisplayTPS()
        {
            //TODO
            return false;
        }

        public bool isDisplayFPS()
        {
            //TODO
            return false;
        }

        public void setTitle(string str)
        {
            //TODO move to frame ?
        }

        public int getWidth()
        {
            return Width;
        }

        public int getHeight()
        {
            return Height;
        }

        #endregion

        private void BattleView_Paint(object sender, PaintEventArgs e)
        {
            Graphics g = e.Graphics;

            //g = Graphics.FromImage(resources.images.body);
/*
            g.RotateTransform(15);
            g.TranslateTransform(100, 100);
            e.Graphics.DrawImage(resources.images.body, new Point(100, 100));
            g.ResetTransform();

            g.RotateTransform(65);
            g.TranslateTransform(100, 40);
            e.Graphics.DrawImage(resources.images.body, new Point(100, 40));
            g.ResetTransform();*/


            //// Test of RenderImage ////

            // Initialize our render image
            Image img = resources.images.ground.blue_metal[0];
            RenderImage renderImage = new RenderImage(img, 0, 0);

            // Prepare translation to move (200, 100)
            Matrix tx = new Matrix();
            tx.Translate(200, 100);

            // Rotate 37 deg
            tx.Rotate(37);

            // Now scale it by 2x3
            tx.Scale(2, 3);

            // Set the final transform on the render image
            renderImage.Transform = tx;

            // Paint our render image
            renderImage.Paint(g);

            // Hmm.. let's paint it's bounding box
            g.DrawRectangle(new Pen(Color.Red), renderImage.Bounds);

//            DrawBattle(g);
        }

        private void DrawBattle(Graphics g)
        {
            DrawGround(g);
        }

        private void DrawGround(Graphics g)
        {
            if (!drawGround)
            {
                g.Clear(Color.Black);
            }
            else
            {
                // Create pre-rendered ground image if it is not available
                if (groundImage == null)
                {
                    CreateGroundImage();
                }

                // Draw the pre-rendered ground if it is available
                if (groundImage != null)
                {
                    int groundWidth = (int)(battleField.getWidth() * scale) + 1;
                    int groundHeight = (int)(battleField.getHeight() * scale) + 1;

                    int dx = (getWidth() - groundWidth) / 2;
                    int dy = (getHeight() - groundHeight) / 2;

//                    AffineTransform savedTx = g.getTransform();

//                    g.setTransform(new AffineTransform());

                    g.DrawImageUnscaled(groundImage, 0, 0);

//                    g.setTransform(savedTx);
                }
            }
        }

        private void CreateGroundImage()
        {
		    // Reinitialize ground tiles

		    int NUM_HORZ_TILES = battleField.getWidth() / groundTileWidth + 1;
		    int NUM_VERT_TILES = battleField.getHeight() / groundTileHeight + 1;

            if ((groundTiles == null) || (groundTiles.GetLength(0) != NUM_HORZ_TILES) || (groundTiles.GetLength(1) != NUM_VERT_TILES))
            {
			    groundTiles = new int[NUM_HORZ_TILES, NUM_VERT_TILES];

                Random random = new Random();

			    for (int y = NUM_VERT_TILES - 1; y >= 0; y--)
                {
				    for (int x = NUM_HORZ_TILES - 1; x >= 0; x--)
                    {
                        groundTiles[x, y] = random.Next(4);
				    }
			    }
		    }

            // Create new buffered image with the ground pre-rendered

            int groundWidth = (int)(battleField.getWidth() * scale);
            int groundHeight = (int)(battleField.getHeight() * scale);

            groundImage = new Bitmap(groundWidth, groundHeight, PixelFormat.Format24bppRgb);

            Graphics groundGfx = Graphics.FromImage(groundImage);

            // TODO: Scale the gfx on the bitmap

            for (int y = NUM_VERT_TILES - 1; y >= 0; y--)
            {
                for (int x = NUM_HORZ_TILES - 1; x >= 0; x--)
                {
                    Bitmap img = resources.images.ground.blue_metal[groundTiles[x, y]];
                    if (img != null)
                    {
                        groundGfx.DrawImageUnscaled(img, x * groundTileWidth, y * groundTileHeight);
                    }
                }
            }
        }
    }
}
