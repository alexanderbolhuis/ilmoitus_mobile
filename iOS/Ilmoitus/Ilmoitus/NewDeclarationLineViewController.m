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
#import "NewDeclarationViewController.h"

@interface NewDeclarationLineViewController ()
@property (weak, nonatomic) IBOutlet UITextField *dateField;
@property (weak, nonatomic) IBOutlet UITextField *typeField;
@property (weak, nonatomic) IBOutlet UITextField *subtypeField;
@property DeclarationLine *line;
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

- (IBAction)addAttachment:(id)sender {
    // TODO deprecated
    [self presentModalViewController:imagePicker animated:YES];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
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
    imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    
    // create blank line
    _dateField.text = @"15-05-2014";
    _line = [[DeclarationLine alloc] init];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([[segue identifier] isEqualToString:@"lineadded"])
    {
        _line.cost = [_costField.text floatValue];
        _line.date = @"2014-05-15 07:27:33.448849";
        // Todo get subtypes
        _line.subtype = 4957078112174080;
        
        [_declaration.lines addObject:_line];
        
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