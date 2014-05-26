//
//  NewDeclarationLineViewController.m
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 15-05-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "NewDeclarationLineViewController.h"
#import "Declaration.h"
#import "DeclarationLine.h"
#import "Attachment.h"
#import "NewDeclarationViewController.h"

@interface NewDeclarationLineViewController ()
@property (weak, nonatomic) IBOutlet UITextField *dateField;
@property (weak, nonatomic) IBOutlet UITextField *typeField;
@property (weak, nonatomic) IBOutlet UITextField *subtypeField;
@property DeclarationLine *line;
@property Attachment *attachment;
@property (weak, nonatomic) IBOutlet UITextField *costField;
@end

@implementation NewDeclarationLineViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    switch (buttonIndex) {
        case 0:
            [self addAttachmentFromPhotoLibrary];
            break;
        case 1:
            [self addAttachmentFromCamera];
            break;
        default:
            break;
    }
}

- (IBAction)addAttachment:(id)sender {
    [[[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:@"Close" destructiveButtonTitle:nil otherButtonTitles:@"Choose existing", @"Create new",  nil]showInView:self.view];
}

-(void)addAttachmentFromPhotoLibrary
{
    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary])
    {
        imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        [self presentImagePicker];
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"Error" message:@"Photo Library is not available" delegate:nil cancelButtonTitle:@"Close" otherButtonTitles:nil];
        [alert show];
    }
}

-(void)addAttachmentFromCamera
{
    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera])
    {
        imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
        [self presentImagePicker];
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"Error" message:@"Camera is not available" delegate:nil cancelButtonTitle:@"Close" otherButtonTitles:nil];
        [alert show];
    }
}

-(void)presentImagePicker
{
    // Depreciated method removed
    [self presentViewController:imagePicker animated:YES completion:nil];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

-(void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    NSURL *imageURL = (NSURL *)[info valueForKey:UIImagePickerControllerReferenceURL];
    
    UIImage *image = (UIImage *)[info valueForKey:UIImagePickerControllerOriginalImage];
    
    _attachment = [[Attachment alloc]init];
    [_attachment SetAttachmentDataFromImage:image];
    _attachment.name = [imageURL path];
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)viewDidLoad
{
    imagePicker = [[UIImagePickerController alloc]init];
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    _dateField.delegate = self;
    _costField.delegate = self;
    _typeField.delegate = self;
    _subtypeField.delegate = self;
    imagePicker.delegate = self;
    
    // Create blank line
    
    // TODO get date from datepicker(datefField) in right format
    _dateField.text = @"15-05-2014";
    _line = [[DeclarationLine alloc] init];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([[segue identifier] isEqualToString:@"lineadded"])
    {
        _line.cost = [_costField.text floatValue];
        // TODO get date from datepicker(datefField) in right format
        _line.date = @"2014-05-15 07:27:33.448849";
        // Todo get subtypes
        _line.subtype = 4519529661071360;
        
        [_declaration.lines addObject:_line];
        if(_attachment != nil)
        {
            [_declaration.attachments addObject:_attachment];
        }
        
        NewDeclarationViewController *declarationController =
        [segue destinationViewController];
        
        declarationController.declaration = _declaration;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
 {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */

@end